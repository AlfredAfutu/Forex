package com.revolut.finance.domain.mapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.revolut.finance.data.source.webservice.dto.CurrenciesResponse
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.mapper.Mapper

class ModelMapper : Mapper<CurrenciesResponse, Currencies> {
    override fun mapTo(input: CurrenciesResponse?): Currencies {
        return Currencies(input?.rates?.map { Rate(it.currency, it.amount) } ?: emptyList())
    }
}