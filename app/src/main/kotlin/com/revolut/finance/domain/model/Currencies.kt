package com.revolut.finance.domain.model

data class Currencies(val rates: List<Rate>)

data class Rate(val currency: String, val amount: Double)