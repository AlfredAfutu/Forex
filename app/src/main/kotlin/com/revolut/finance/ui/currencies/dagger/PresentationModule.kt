package com.revolut.finance.ui.currencies.dagger

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.revolut.finance.RevolutApplication
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.currencies.CurrenciesActivity
import com.revolut.finance.ui.currencies.CurrenciesFragment
import com.revolut.finance.ui.currencies.CurrenciesViewModel
import com.revolut.finance.ui.currencies.mapper.ModelMapper
import com.revolut.finance.ui.currencies.model.CurrenciesViewData
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [ViewModule::class, ViewModelModule::class])
object PresentationModule {

    @Provides
    @JvmStatic
    @Reusable
    fun provideModelMapper(): Mapper<Currencies, CurrenciesViewData> = ModelMapper()
}

@Module
interface ViewModule {
    @ContributesAndroidInjector
    fun contributeCurrenciesFragment(): CurrenciesFragment
}

@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(CurrenciesViewModel::class)
    fun bindCurrenciesViewModel(viewModel: CurrenciesViewModel): ViewModel

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}