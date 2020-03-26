package com.revolut.finance.ui.model

data class CurrenciesViewData(val rates: List<RateViewData> = emptyList()) {
    companion object { val EMPTY = CurrenciesViewData() }
}

data class RateViewData(
    val currency: String,
    val flag: String,
    val amount: String,
    val currencyDisplayName: String
)