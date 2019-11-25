package com.revolut.finance.ui.currencies.mapper

import android.icu.util.Currency
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.currencies.model.CurrenciesViewData
import com.revolut.finance.ui.currencies.model.RateViewData

class ModelMapper : Mapper<Currencies, CurrenciesViewData> {
    override fun mapTo(input: Currencies?): CurrenciesViewData {
        return CurrenciesViewData(input?.rates?.map { rate ->
            val displayName = Currency.getAvailableCurrencies().first {
                it.currencyCode == rate.currency
            }.displayName
            RateViewData(
                rate.currency,
                rate.currency.slice(0..1),
                "${rate.amount}",
                displayName
            )
        } ?: emptyList())
    }
}