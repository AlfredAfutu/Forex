package com.revolut.finance.data.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.revolut.finance.BaseTest
import com.revolut.finance.data.source.local.cache.ICurrenciesCache
import com.revolut.finance.data.source.remote.api.ApiResult
import com.revolut.finance.data.source.remote.api.CurrenciesApi
import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import com.revolut.finance.domain.mapper.ModelMapper
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.Rate
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import retrofit2.adapter.rxjava2.Result
import java.math.BigDecimal

internal class CurrenciesRepositoryTest : BaseTest() {

    private val api: CurrenciesApi = mock()
    private val cache: ICurrenciesCache = mock()
    private val mapper: Mapper<CurrenciesResponse, Currencies> = ModelMapper()

    private lateinit var repository: ICurrenciesRepository

    @BeforeEach
    override fun setup() {
        super.setup()
        repository = CurrenciesRepository(api, cache, mapper)

        whenever(api.getCurrenciesWithRates()).thenReturn(Single.never())
    }

    @Test
    fun `when getCurrenciesFromApi is called, call api getCurrenciesWithRates`() {
        repository.getCurrenciesFromApi()

        verify(api).getCurrenciesWithRates()
    }

    @Test
    fun `when getCurrenciesFromCache is called, call cache get currencies`() {
        repository.getCurrenciesFromCache()

        verify(cache).currencies
    }

    @Test
    fun `when api getCurrenciesWithRates emits an error, return ApiResult of NetworkError`() {
        whenever(api.getCurrenciesWithRates()).thenReturn(Single.just(Result.error(RuntimeException())))
        repository.getCurrenciesFromApi()
            .test()
            .assertValue { it is ApiResult.NetworkError }
    }

    @Test
    fun `when api getCurrenciesWithRates emits a response with null body, return ApiResult of Success`() {
        whenever(api.getCurrenciesWithRates()).thenReturn(Single.just(Result.response(Response.success<CurrenciesResponse>(null))))
        repository.getCurrenciesFromApi()
            .test()
            .assertValue { it is ApiResult.GeneralError }
    }

    @Test
    fun `when api getCurrenciesWithRates emits a response, return ApiResult of Success`() {
        val response = CurrenciesResponse(base = "AUD")
        val expectedRates = listOf(Rate("AUD", BigDecimal("1")))
        whenever(api.getCurrenciesWithRates()).thenReturn(Single.just(Result.response(Response.success(response))))
        repository.getCurrenciesFromApi()
            .test()
            .assertValue { it is ApiResult.Success }
            .assertValue(ApiResult.Success(Currencies("AUD", expectedRates)))
    }
}