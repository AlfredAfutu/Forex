package com.revolut.finance.domain.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.inOrder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.revolut.currencies.R
import com.revolut.finance.BaseTest
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.domain.model.sortWithExistingTop
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class MoveCurrencyToTopUseCaseTest : BaseTest() {

    private val repository: ICurrenciesRepository = mock()
    private val mapper: Mapper<Currencies, CurrenciesViewData> = mock()

    private lateinit var useCase: UseCase<String, UIResult<CurrenciesViewData>>

    @BeforeEach
    override fun setup() {
        super.setup()
        useCase = MoveCurrencyToTopUseCase(repository, mapper)
    }

    @Test
    fun `when usecase is invoked without subscribing, do not interact with any dependency`() {
        useCase("currency")

        verifyZeroInteractions(repository)
        verifyZeroInteractions(mapper)
    }

    @Test
    fun `when usecase is invoked with observer subscribed, call repository setTopCurrency, repo getCurrenciesFromCache and mapper mapTo`() {
        val currency = "EUR"
        val currencies = Currencies("GBP", listOf(Rate(currency, BigDecimal("2.34"))))
        whenever(repository.getCurrenciesFromCache()).thenReturn(currencies)
        whenever(repository.getTopCurrency()).thenReturn(currency)
        whenever(mapper.mapTo(any())).thenReturn(CurrenciesViewData.EMPTY)
        useCase(currency)
            .test()
            .also { triggerActions() }

        inOrder(repository, mapper) {
            verify(repository).setTopCurrency(currency)
            verify(repository).getCurrenciesFromCache()
            verify(mapper).mapTo(any())
        }
    }

    @Test
    fun `when usecase is invoked with observer subscribed, call mapper with currencies with topCurrency at first position`() {
        val topCurrency = "EUR"
        val baseCurrency = "GBP"
        val currencies = Currencies(
            baseCurrency,
            listOf(Rate("AUD", BigDecimal("9.23")), Rate(topCurrency, BigDecimal("2.34")))
        )
        val expectedCurrencies = Currencies(
            baseCurrency,
            listOf(Rate(topCurrency, BigDecimal("2.34")), Rate("AUD", BigDecimal("9.23")))
        )
        whenever(repository.getCurrenciesFromCache()).thenReturn(currencies)
        whenever(repository.getTopCurrency()).thenReturn(topCurrency)
        whenever(mapper.mapTo(any())).thenReturn(CurrenciesViewData.EMPTY)
        useCase(topCurrency)
            .test()
            .also { triggerActions() }

        verify(mapper).mapTo(expectedCurrencies)
    }

    @Test
    fun `when repo getCurrenciesFromCache throws an error, terminate subscription`() {
        whenever(repository.getCurrenciesFromCache()).thenThrow(RuntimeException("Currencies is null"))
        useCase("currency")
            .test()
            .also { triggerActions() }
            .assertValue(UIResult.Error(R.string.move_currency_to_top_error_message))
    }

    @Test
    fun `when repo getTopCurrency throws an error, terminate subscription`() {
        whenever(repository.getTopCurrency()).thenThrow(RuntimeException("Top currency is null"))
        useCase("currency")
            .test()
            .also { triggerActions() }
            .assertValue(UIResult.Error(R.string.move_currency_to_top_error_message))
    }

    @Test
    fun `when mapper mapTo throws an error, terminate subscription`() {
        whenever(mapper.mapTo(any())).thenThrow(RuntimeException("Argument is null"))
        useCase("currency")
            .test()
            .also { triggerActions() }
            .assertValue(UIResult.Error(R.string.move_currency_to_top_error_message))
    }

    @Test
    fun `when currencies sortedWithExistingTop throws an error, terminate subscription`() {
        val topCurrency = "USD"
        val currencies = mock<Currencies>()
        whenever(repository.getCurrenciesFromCache()).thenReturn(currencies)
        whenever(currencies.sortWithExistingTop(topCurrency)).thenThrow(RuntimeException("Error sorting currencies"))
        useCase(topCurrency)
            .test()
            .also { triggerActions() }
            .assertValue(UIResult.Error(R.string.move_currency_to_top_error_message))
    }
}