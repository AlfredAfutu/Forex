package com.revolut.finance.dagger

import com.revolut.finance.RevolutApplication
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Component(modules = [AppModule::class])
@Singleton
interface AppComponent : AndroidInjector<RevolutApplication> {
    @Component.Factory
    interface Builder : AndroidInjector.Factory<RevolutApplication>
}