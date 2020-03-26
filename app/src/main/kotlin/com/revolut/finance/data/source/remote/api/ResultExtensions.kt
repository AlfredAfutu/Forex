package com.revolut.finance.data.source.remote.api

import io.reactivex.Single
import retrofit2.adapter.rxjava2.Result

fun <T> Single<Result<T>>.withErrorHandling(): Single<ApiResult<T>> {
    return this.map {
        val response = it.response()
        when {
            !(response?.isSuccessful ?: true) || it.isError -> { ApiResult.NetworkError(it.error()) }
            response?.body() == null -> ApiResult.GeneralError
            else -> ApiResult.Success(response.body())
        }
    }
}

fun <T, U> Single<ApiResult<T>>.withSuccess(block: (obj: T?) -> ApiResult.Success<U>): Single<ApiResult<U>> {
    return this.map {
        when (it) {
            is ApiResult.NetworkError -> it
            is ApiResult.GeneralError -> it
            is ApiResult.Success -> block(it.value)
        }
    }
}
