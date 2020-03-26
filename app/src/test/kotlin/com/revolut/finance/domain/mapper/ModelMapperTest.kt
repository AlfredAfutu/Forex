package com.revolut.finance.domain.mapper

import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import com.revolut.finance.data.source.remote.api.dto.RateResponse
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.mapper.Mapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ModelMapperTest {

    private val mapper: Mapper<CurrenciesResponse, Currencies> = ModelMapper()

    @Test
    fun `when input is null, return empty Currencies object`() {
        val mappedValue = mapper.mapTo(null)

        Assertions.assertEquals(Currencies.EMPTY, mappedValue)
    }

    @Test
    fun `when input rates is null, return Currencies object with only base currency rate`() {
        val baseCurrency = "GBP"
        val expectedValue = Currencies(baseCurrency = baseCurrency, rates = listOf(Rate(baseCurrency, BigDecimal("1"))))
        val mappedValue = mapper.mapTo(CurrenciesResponse(base = baseCurrency, rates = null))

        Assertions.assertEquals(expectedValue, mappedValue)
    }

    @Test
    fun `when input base is null, return Currencies object with empty baseCurrency`() {
        val expectedValue = Currencies(baseCurrency = "", rates = listOf(Rate("", BigDecimal("1"))))
        val mappedValue = mapper.mapTo(CurrenciesResponse(base = null, rates = listOf()))

        Assertions.assertEquals(expectedValue, mappedValue)
    }

    @Test
    fun `when input rates is not empty, return Currencies object with rates plus base rate`() {
        val baseCurrency = "USD"
        val rates = listOf(RateResponse("AUD", 43.0))
        val expectedValue = Currencies(
            baseCurrency = baseCurrency,
            rates = listOf(Rate(baseCurrency, BigDecimal("1")), Rate("AUD", BigDecimal("43.0"))))
        val mappedValue = mapper.mapTo(CurrenciesResponse(base = baseCurrency, rates = rates))

        Assertions.assertEquals(expectedValue, mappedValue)
    }
}