package com.revolut.finance.data.repository

import com.revolut.finance.data.source.webservice.api.CurrenciesApi
import com.revolut.finance.data.source.webservice.api.Result
import com.revolut.finance.data.source.webservice.api.withErrorHandling
import com.revolut.finance.data.source.webservice.api.withSuccess
import com.revolut.finance.data.source.webservice.dto.CurrenciesResponse
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import io.reactivex.Single

class CurrenciesRepository(
    private val currenciesApi: CurrenciesApi,
    private val mapper: Mapper<CurrenciesResponse, Currencies>
) : ICurrenciesRepository {

    override fun getCurrencies(): Single<Result<Currencies>> {
        return currenciesApi.getCurrenciesWithRates()
            .withErrorHandling()
            .withSuccess { Result.Success(mapper.mapTo(it)) }
    }
}