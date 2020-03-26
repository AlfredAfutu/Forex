package com.revolut.finance.domain.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.revolut.currencies.R
import com.revolut.finance.BaseTest
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.model.RateViewData
import com.revolut.finance.ui.model.UpdateRatesData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.math.BigDecimal

internal class UpdateRatesUseCaseTest : BaseTest() {

    private val repository: ICurrenciesRepository = mock()
    private val mapper: Mapper<Currencies, CurrenciesViewData> = mock()

    private lateinit var usecase: UseCase<UpdateRatesData, UIResult<List<RateViewData>>>

    @BeforeEach
    override fun setup() {
        super.setup()
        usecase = UpdateRatesUseCase(repository, mapper)
    }

    @Test
    fun `when usecase is invoked without subscribing, do not interact with any dependency`() {
        usecase(UpdateRatesData())

        verifyZeroInteractions(repository)
        verifyZeroInteractions(mapper)
    }

    @Test
    fun `when usecase throws an error, return UIResult of error`() {
        whenever(repository.getCurrenciesFromCache()).thenThrow(RuntimeException())

        usecase(UpdateRatesData())
            .test()
            .also { triggerActions() }
            .assertValue { it is UIResult.Error && it.errorMessage == R.string.rate_update_error_message }
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "0.0"])
    fun `when newRate is blank or zero, map all currency rates as zero but do not update currencies in repo`(newRate: String) {
        val currencies = Currencies("EUR", listOf(Rate("USD", BigDecimal("1.0")), Rate("GBP", BigDecimal("0.38"))))
        val updatedCurrencies = Currencies("EUR", listOf(Rate("USD", BigDecimal("0")), Rate("GBP", BigDecimal("0"))))
        whenever(repository.getCurrenciesFromCache()).thenReturn(currencies)

        usecase(UpdateRatesData(newRate = newRate))
            .test()
            .also { triggerActions() }

        verify(mapper).mapTo(updatedCurrencies)
        verify(repository, never()).setCacheCurrencies(any())
    }

    @Test
    fun `when newRate is not blank and not zero, map all currency rates and update currencies in repo`() {
        val currencies = Currencies("EUR", listOf(Rate("USD", BigDecimal("1.0")), Rate("GBP", BigDecimal("2.0"))))
        val updatedCurrencies = Currencies("EUR", listOf(Rate("USD", BigDecimal("4.00")), Rate("GBP", BigDecimal("8.00"))))
        whenever(repository.getCurrenciesFromCache()).thenReturn(currencies)

        usecase(UpdateRatesData(newRate = "4.0", currency = "USD"))
            .test()
            .also { triggerActions() }

        inOrder(repository, mapper) {
            verify(repository).setCacheCurrencies(updatedCurrencies)
            verify(mapper).mapTo(any())
        }
    }
}