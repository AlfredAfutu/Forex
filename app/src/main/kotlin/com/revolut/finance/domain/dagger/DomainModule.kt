package com.revolut.finance.domain.dagger

import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import com.revolut.finance.domain.mapper.ModelMapper
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.domain.usecase.GetCurrenciesWithRatesUseCase
import com.revolut.finance.domain.usecase.MoveCurrencyToTopUseCase
import com.revolut.finance.domain.usecase.UpdateRatesUseCase
import com.revolut.finance.domain.usecase.UseCase
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.model.RateViewData
import com.revolut.finance.ui.model.UpdateRatesData
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
object DomainModule {

    @Provides
    @Reusable
    fun provideMapper(): Mapper<CurrenciesResponse, Currencies> = ModelMapper()

    @Provides
    @Reusable
    fun provideGetCurrenciesWithRatesUseCase(
        repository: ICurrenciesRepository,
        mapper: Mapper<Currencies, CurrenciesViewData>
    ): UseCase<Unit, UIResult<CurrenciesViewData>> = GetCurrenciesWithRatesUseCase(repository, mapper)

    @Provides
    @Reusable
    fun provideUpdateRatesUseCase(
        repository: ICurrenciesRepository,
        mapper: Mapper<Currencies, CurrenciesViewData>
    ): UseCase<UpdateRatesData, UIResult<List<RateViewData>>> = UpdateRatesUseCase(repository, mapper)

    @Provides
    @Reusable
    fun provideMoveCurrencyToTopUseCase(
        repository: ICurrenciesRepository,
        mapper: Mapper<Currencies, CurrenciesViewData>
    ): UseCase<String, UIResult<CurrenciesViewData>> = MoveCurrencyToTopUseCase(repository, mapper)
}