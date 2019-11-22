package com.revolut.finance.data.source.webservice.api

sealed class Result<out T> {
    data class Success<T>(val value: T?) : Result<T>()
    data class NetworkError(val throwable: Throwable?) : Result<Nothing>()
    object GeneralError : Result<Nothing>()
}