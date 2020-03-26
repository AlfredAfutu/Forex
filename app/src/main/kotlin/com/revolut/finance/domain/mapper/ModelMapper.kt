package com.revolut.finance.domain.mapper

import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.mapper.Mapper
import java.math.BigDecimal

class ModelMapper : Mapper<CurrenciesResponse, Currencies> {
    override fun mapTo(input: CurrenciesResponse?): Currencies {
        return input?.let { response ->
            val rates = mutableListOf<Rate>().apply {
                add(Rate(mapBase(response), BigDecimal("1")))
                response.rates?.map { rate ->
                    Rate(rate.currency, BigDecimal((rate.amount ?: 0.0).toString()))
                }?.let { rates -> addAll(rates) }
            }
            Currencies(mapBase(response), rates)
        } ?: Currencies.EMPTY
    }

    private fun mapBase(response: CurrenciesResponse): String = response.base ?: ""
}