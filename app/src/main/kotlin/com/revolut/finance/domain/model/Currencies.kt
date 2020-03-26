package com.revolut.finance.domain.model

import java.math.BigDecimal

data class Currencies(val baseCurrency: String = "", val rates: List<Rate> = emptyList()) {
    companion object { val EMPTY = Currencies() }
}

data class Rate(val currency: String, val amount: BigDecimal)

fun Currencies.sortWithExistingTop(topCurrency: String): Currencies? {
    return rates.find { it.currency == topCurrency }?.let {
        val currencyRates = rates.toMutableList().apply {
            remove(it)
            add(0, it)
        }
        copy(rates = currencyRates)
    }
}