package com.revolut.finance.data.source.local.cache

import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class CurrenciesCacheTest {

    private val currenciesCache: ICurrenciesCache = CurrenciesCache()

    @Test
    fun `when getTopCurrency is called without being set, return empty`() {
        Assertions.assertTrue(currenciesCache.topCurrency.isEmpty())
    }

    @Test
    fun `when getTopCurrency is called being set, return value`() {
        val currency = "AUD"
        currenciesCache.topCurrency = currency

        Assertions.assertEquals(currency, currenciesCache.topCurrency)
    }

    @Test
    fun `when getCurrenciesFromCache is called without being set, return empty Currencies`() {
        Assertions.assertEquals(Currencies.EMPTY, currenciesCache.currencies)
    }

    @Test
    fun `when getCurrencies is called being set, return value`() {
        val currencies = Currencies(baseCurrency = "USD", rates = listOf(Rate("AUD", BigDecimal("66.7"))))
        currenciesCache.currencies = currencies

        Assertions.assertEquals(currencies.baseCurrency, currenciesCache.currencies.baseCurrency)
        Assertions.assertEquals(currencies.rates, currenciesCache.currencies.rates)
    }
}