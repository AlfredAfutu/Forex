package com.revolut.finance.domain.dagger

import com.revolut.finance.data.repository.ICurrenciesRepository
import com.revolut.finance.data.source.webservice.dto.CurrenciesResponse
import com.revolut.finance.domain.mapper.ModelMapper
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.usecase.GetCurrenciesWithRatesUsecase
import com.revolut.finance.domain.usecase.IGetCurrenciesWithRatesUsecase
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.currencies.model.CurrenciesViewData
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
object DomainModule {

    @Provides
    @JvmStatic
    @Reusable
    fun provideMapper(): Mapper<CurrenciesResponse, Currencies> = ModelMapper()

    @Provides
    @JvmStatic
    @Reusable
    fun provideGetCurrenciesWithRatesInteractor(
        repository: ICurrenciesRepository,
        mapper: Mapper<Currencies, CurrenciesViewData>
    ): IGetCurrenciesWithRatesUsecase = GetCurrenciesWithRatesUsecase(repository, mapper)
}