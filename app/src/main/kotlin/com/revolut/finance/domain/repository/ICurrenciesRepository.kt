package com.revolut.finance.domain.repository

import com.revolut.finance.data.source.remote.api.ApiResult
import com.revolut.finance.domain.model.Currencies
import io.reactivex.Single

interface ICurrenciesRepository {
    fun getTopCurrency(): String
    fun setTopCurrency(currency: String)
    fun getCurrenciesFromCache(): Currencies
    fun setCacheCurrencies(currencies: Currencies)
    fun getCurrenciesFromApi(): Single<ApiResult<Currencies>>
}