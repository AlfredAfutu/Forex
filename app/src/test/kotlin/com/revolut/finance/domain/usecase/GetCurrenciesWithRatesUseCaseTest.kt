package com.revolut.finance.domain.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.revolut.currencies.R
import com.revolut.finance.BaseTest
import com.revolut.finance.data.source.remote.api.ApiResult
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

internal class GetCurrenciesWithRatesUseCaseTest : BaseTest() {

    private val repository: ICurrenciesRepository = mock()
    private val mapper: Mapper<Currencies, CurrenciesViewData> = mock()

    private lateinit var useCase: UseCase<Unit, UIResult<CurrenciesViewData>>

    @BeforeEach
    override fun setup() {
        super.setup()
        useCase = GetCurrenciesWithRatesUseCase(repository, mapper)
        whenever(repository.getCurrenciesFromApi()).thenReturn(Single.never())
        whenever(repository.getCurrenciesFromCache()).thenReturn(Currencies.EMPTY)
    }

    @Test
    fun `when usecase is invoked without subscribing, do not interact with any dependency`() {
        useCase(Unit)

        verifyZeroInteractions(repository)
        verifyZeroInteractions(mapper)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3, 4, 5, 100])
    fun `when usecase is invoked and observer subscribed, call repo getCurrenciesFromApi every second`(
        timeInSeconds: Int
    ) {
        useCase(Unit)
            .test()
            .also { advanceSchedulerAndTriggerActions(timeInSeconds.toLong()) }

        verify(repository, times(timeInSeconds)).getCurrenciesFromApi()
    }

    @Test
    fun `when repo getCurrenciesFromApi returns an ApiResult NetworkError, return UIResult of Error with appropriate message`() {
        whenever(repository.getCurrenciesFromApi()).thenReturn(
            Single.just(
                ApiResult.NetworkError(
                    RuntimeException()
                )
            )
        )

        useCase(Unit)
            .test()
            .also { advanceSchedulerAndTriggerActions() }
            .assertValue {
                it is UIResult.Error &&
                        it.errorMessage == R.string.network_error_message
            }
    }

    @Test
    fun `when repo getCurrenciesFromApi returns an ApiResult GeneralError, return UIResult of Error with appropriate message`() {
        whenever(repository.getCurrenciesFromApi()).thenReturn(Single.just(ApiResult.GeneralError))

        useCase(Unit)
            .test()
            .also { advanceSchedulerAndTriggerActions() }
            .assertValue {
                it is UIResult.Error &&
                        it.errorMessage == R.string.bad_response_message
            }
    }

    @Test
    fun `when repo getCurrenciesFromApi returns an ApiResult Success with null value, map currencies`() {
        whenever(repository.getCurrenciesFromApi()).thenReturn(Single.just(ApiResult.Success(null)))
        whenever(repository.getTopCurrency()).thenReturn("")

        useCase(Unit)
            .test()
            .also { advanceSchedulerAndTriggerActions() }

        verify(repository).setCacheCurrencies(Currencies.EMPTY)
        verify(mapper).mapTo(any())
    }

    @Test
    fun `when repo getCurrenciesFromApi returns an ApiResult Success with value, map Currencies`() {
        val currencies = Currencies(rates = listOf(Rate("GBP", BigDecimal("23.42")), Rate("EUR", BigDecimal("50.234"))))
        val sortedCurrencies = Currencies(rates = listOf(Rate("EUR", BigDecimal("50.234")), Rate("GBP", BigDecimal("23.42"))))
        whenever(repository.getTopCurrency()).thenReturn("EUR")
        whenever(repository.getCurrenciesFromApi()).thenReturn(Single.just(ApiResult.Success(currencies)))

        useCase(Unit)
            .test()
            .also { advanceSchedulerAndTriggerActions() }

        verify(repository).setCacheCurrencies(sortedCurrencies)
        verify(mapper).mapTo(any())
    }
}