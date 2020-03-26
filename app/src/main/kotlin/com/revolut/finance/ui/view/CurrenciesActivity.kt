package com.revolut.finance.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.revolut.currencies.R

class CurrenciesActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.currencies_activity)
    }
}
