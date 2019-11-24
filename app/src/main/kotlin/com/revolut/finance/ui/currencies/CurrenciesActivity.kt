package com.revolut.finance.ui.currencies

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.revolut.currencies.R
import dagger.android.AndroidInjection

class CurrenciesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.currencies_activity)
    }

}
