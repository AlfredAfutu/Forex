package com.revolut.finance.data.source.webservice.api

import com.revolut.finance.data.source.webservice.dto.CurrenciesResponse
import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_CURRENCY = "EUR"

interface CurrenciesApi {
    @GET("/latest")
    fun getCurrenciesWithRates(@Query("base") base: String = BASE_CURRENCY): Single<Result<CurrenciesResponse>>
}