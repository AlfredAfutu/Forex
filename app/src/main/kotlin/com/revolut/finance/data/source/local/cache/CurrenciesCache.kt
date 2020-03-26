package com.revolut.finance.data.source.local.cache

import com.revolut.finance.domain.model.Currencies

class CurrenciesCache : ICurrenciesCache {

    override var topCurrency: String = ""

    override var currencies: Currencies = Currencies.EMPTY
        @Synchronized set
}