package com.revolut.finance.ui.mapper

import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.model.RateViewData
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.math.BigDecimal

internal class ModelMapperTest {

    private val mapper: Mapper<Currencies, CurrenciesViewData> = ModelMapper()

    @Test
    fun `when input is null, return empty CurrenciesViewData`() {
        val mappedValue = mapper.mapTo(null)

        Assertions.assertEquals(CurrenciesViewData.EMPTY, mappedValue)
    }

    @Test
    fun `when input rates is empty, return CurrenciesViewData with empty rates`() {
        val mappedValue = mapper.mapTo(Currencies.EMPTY)

        Assertions.assertEquals(CurrenciesViewData(rates = emptyList()), mappedValue)
    }

    @Test
    fun `when input rates is not empty, return CurrenciesViewData with correct rate view data`() {
        val currencies = Currencies("AUD", listOf(Rate("EUR", BigDecimal("1.23")), Rate("GBP", BigDecimal("12.43"))))
        val mappedValue = mapper.mapTo(currencies)
        val expectedValue = CurrenciesViewData(
            listOf(
                RateViewData("EUR", "EU", "1.23", "Euro"),
                RateViewData("GBP", "GB", "12.43", "British Pound Sterling")
            )
        )

        Assertions.assertEquals(currencies.rates.size, mappedValue.rates.size)
        Assertions.assertEquals(expectedValue, mappedValue)
    }
}