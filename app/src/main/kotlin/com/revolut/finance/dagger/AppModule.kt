package com.revolut.finance.dagger

import com.revolut.finance.data.dagger.DataModule
import com.revolut.finance.domain.dagger.DomainModule
import com.revolut.finance.ui.currencies.dagger.PresentationModule
import dagger.Module
import dagger.android.AndroidInjectionModule

@Module(
    includes = [
        AndroidInjectionModule::class,
        PresentationModule::class,
        NetworkModule::class,
        DataModule::class,
        DomainModule::class
    ]
)
object AppModule