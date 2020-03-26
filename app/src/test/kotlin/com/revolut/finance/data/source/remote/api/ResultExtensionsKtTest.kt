package com.revolut.finance.data.source.remote.api

import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import io.reactivex.Single
import org.junit.jupiter.api.Test
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result

internal class ResultExtensionsKtTest {

    @Test
    fun `when withSuccess is called on ApiResult of NetworkError return same`() {
        val apiResult: Single<ApiResult<CurrenciesResponse>> = Single.just(ApiResult.NetworkError(Throwable()))

        apiResult.withSuccess {ApiResult.Success(CurrenciesResponse())}
            .test()
            .assertValue { it is ApiResult.NetworkError }
    }

    @Test
    fun `when withSuccess is called on ApiResult of GeneralError return same`() {
        val apiResult: Single<ApiResult<CurrenciesResponse>> = Single.just(ApiResult.GeneralError)

        apiResult.withSuccess {ApiResult.Success(CurrenciesResponse())}
            .test()
            .assertValue { it is ApiResult.GeneralError }
    }

    @Test
    fun `when withSuccess is called on ApiResult of Success return same`() {
        val apiResult: Single<ApiResult<CurrenciesResponse>> = Single.just(ApiResult.Success(CurrenciesResponse()))

        apiResult.withSuccess {ApiResult.Success(CurrenciesResponse())}
            .test()
            .assertValue { it is ApiResult.Success }
    }

    @Test
    fun `when withErrorHandling is called on Result of error return ApiResult of NetworkError`() {
        val apiResult: Single<Result<CurrenciesResponse>> = Single.just(Result.error(RuntimeException()))

        apiResult.withErrorHandling()
            .test()
            .assertValue { it is ApiResult.NetworkError }
    }

    @Test
    fun `when withErrorHandling is called on Result of response return ApiResult of Success`() {
        val apiResult: Single<Result<CurrenciesResponse>> = Single.just(Result.response(Response.success(CurrenciesResponse())))

        apiResult.withErrorHandling()
            .test()
            .assertValue { it is ApiResult.Success }
    }
}