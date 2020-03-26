package com.revolut.finance

import com.blongho.country_data.World
import com.revolut.finance.dagger.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

open class RevolutApplication : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()
        World.init(applicationContext) //Initialize lib for country flags
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> = DaggerAppComponent.factory().create(this)
}