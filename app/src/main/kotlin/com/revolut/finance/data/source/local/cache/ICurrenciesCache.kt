package com.revolut.finance.data.source.local.cache

import com.revolut.finance.domain.model.Currencies

interface ICurrenciesCache {
    var topCurrency: String
    var currencies: Currencies
}