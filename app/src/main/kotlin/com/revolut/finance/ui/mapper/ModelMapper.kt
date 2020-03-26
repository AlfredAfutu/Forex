package com.revolut.finance.ui.mapper

import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.model.RateViewData
import java.util.*

class ModelMapper : Mapper<Currencies, CurrenciesViewData> {
    override fun mapTo(input: Currencies?): CurrenciesViewData {
        return input?.let { currencies ->
            CurrenciesViewData(
                currencies.rates.map { rate ->
                    val displayName = Currency.getAvailableCurrencies().first { it.currencyCode == rate.currency }.displayName
                    RateViewData(rate.currency, rate.currency.slice(0..1), "${rate.amount}", displayName)
                }
            )
        } ?: CurrenciesViewData.EMPTY
    }
}