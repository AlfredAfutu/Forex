package com.revolut.finance.data.source.webservice.api

import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

fun <T> Single<Result<T>>.withErrorHandling(): Single<com.revolut.finance.data.source.webservice.api.Result<T>> {
    return this.map {
        val response = it.response()
        when {
            response?.isSuccessful == false || it.isError -> {
                com.revolut.finance.data.source.webservice.api.Result.NetworkError(it.error())
            }
            response?.body() == null -> com.revolut.finance.data.source.webservice.api.Result.GeneralError
            else -> com.revolut.finance.data.source.webservice.api.Result.Success<T>(response.body())
        }
    }
}


fun <T, U> Single<com.revolut.finance.data.source.webservice.api.Result<T>>.withSuccess(block: (obj: T?) -> com.revolut.finance.data.source.webservice.api.Result.Success<U>): Single<com.revolut.finance.data.source.webservice.api.Result<U>> {
    return this.map {
        when (it) {
            is com.revolut.finance.data.source.webservice.api.Result.NetworkError -> it
            is com.revolut.finance.data.source.webservice.api.Result.GeneralError -> it
            is com.revolut.finance.data.source.webservice.api.Result.Success -> block(it.value)
        }
    }
}
