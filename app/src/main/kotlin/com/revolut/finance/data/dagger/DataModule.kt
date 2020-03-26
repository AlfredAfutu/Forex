package com.revolut.finance.data.dagger

import com.revolut.finance.data.repository.CurrenciesRepository
import com.revolut.finance.data.source.local.cache.CurrenciesCache
import com.revolut.finance.data.source.local.cache.ICurrenciesCache
import com.revolut.finance.data.source.remote.api.CurrenciesApi
import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
object DataModule {

    @Provides
    @Reusable
    fun provideCurrenciesApi(retrofit: Retrofit): CurrenciesApi = retrofit.create(CurrenciesApi::class.java)

    @Provides
    @Reusable
    fun provideCurrenciesRepository(
        api: CurrenciesApi,
        mapper: Mapper<CurrenciesResponse, Currencies>,
        currenciesCache: ICurrenciesCache
    ): ICurrenciesRepository = CurrenciesRepository(api, currenciesCache, mapper)

    @Provides
    @Singleton
    fun provideCurrenciesCache(): ICurrenciesCache = CurrenciesCache()
}