package com.revolut.finance.data.source.remote.api

import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query

private const val QUERY_VALUE = "EUR"
private const val QUERY_KEY = "base"

interface CurrenciesApi {
    @GET("api/android/latest")
    fun getCurrenciesWithRates(@Query(QUERY_KEY) base: String = QUERY_VALUE): Single<Result<CurrenciesResponse>>
}