package com.revolut.finance.data.dagger

import com.revolut.finance.data.repository.CurrenciesRepository
import com.revolut.finance.data.repository.ICurrenciesRepository
import com.revolut.finance.data.source.webservice.api.CurrenciesApi
import com.revolut.finance.data.source.webservice.dto.CurrenciesResponse
import com.revolut.finance.domain.mapper.ModelMapper
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
object DataModule {

    @Provides
    @JvmStatic
    @Reusable
    fun provideCurrenciesApi(retrofit: Retrofit): CurrenciesApi = retrofit.create(CurrenciesApi::class.java)

    @Provides
    @JvmStatic
    @Reusable
    fun provideCurrenciesRepository(
        api: CurrenciesApi,
        mapper: Mapper<CurrenciesResponse, Currencies>
    ): ICurrenciesRepository = CurrenciesRepository(api, mapper)
}