package com.revolut.finance.ui.dagger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.mapper.ModelMapper
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.view.CurrenciesFragment
import com.revolut.finance.ui.viewmodel.CurrenciesViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [ViewModule::class, ViewModelModule::class])
object PresentationModule {

    @Provides
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