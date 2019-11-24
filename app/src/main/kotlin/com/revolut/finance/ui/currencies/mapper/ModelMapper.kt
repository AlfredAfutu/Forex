package com.revolut.finance.ui.currencies.mapper

import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.currencies.model.CurrenciesViewData
import com.revolut.finance.ui.currencies.model.RateViewData

class ModelMapper : Mapper<Currencies, CurrenciesViewData> {
    override fun mapTo(input: Currencies?): CurrenciesViewData {
        return CurrenciesViewData(input?.rates?.map { RateViewData(it.currency, it.amount) } ?: emptyList())
    }
}