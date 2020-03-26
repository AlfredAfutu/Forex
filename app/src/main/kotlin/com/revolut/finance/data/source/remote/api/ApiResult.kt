package com.revolut.finance.data.source.remote.api

sealed class ApiResult<out T> {
    data class Success<T>(val value: T?) : ApiResult<T>()
    data class NetworkError(val throwable: Throwable?) : ApiResult<Nothing>()
    object GeneralError : ApiResult<Nothing>()
}