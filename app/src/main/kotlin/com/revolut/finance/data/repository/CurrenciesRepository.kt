package com.revolut.finance.data.repository

import com.revolut.finance.data.source.local.cache.ICurrenciesCache
import com.revolut.finance.data.source.remote.api.ApiResult
import com.revolut.finance.data.source.remote.api.CurrenciesApi
import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import com.revolut.finance.data.source.remote.api.withErrorHandling
import com.revolut.finance.data.source.remote.api.withSuccess
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import io.reactivex.Single

class CurrenciesRepository(
    private val currenciesApi: CurrenciesApi,
    private val currenciesCache: ICurrenciesCache,
    private val mapper: Mapper<CurrenciesResponse, Currencies>
) : ICurrenciesRepository {

    override fun getTopCurrency(): String = currenciesCache.topCurrency

    override fun setTopCurrency(currency: String) {
        currenciesCache.topCurrency = currency
    }

    override fun getCurrenciesFromCache(): Currencies = currenciesCache.currencies

    override fun setCacheCurrencies(currencies: Currencies) {
        currenciesCache.currencies = currencies
    }

    override fun getCurrenciesFromApi(): Single<ApiResult<Currencies>> {
        return currenciesApi.getCurrenciesWithRates()
            .withErrorHandling()
            .withSuccess { response -> ApiResult.Success(mapper.mapTo(response)) }
    }
}