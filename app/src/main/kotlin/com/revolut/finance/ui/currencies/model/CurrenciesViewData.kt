package com.revolut.finance.ui.currencies.model

data class CurrenciesViewData(val rates: List<RateViewData>)

data class RateViewData(val currency: String, val amount: Double)