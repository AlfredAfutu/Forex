package com.revolut.finance.data.repository

import com.revolut.finance.data.source.webservice.api.Result
import com.revolut.finance.domain.model.Currencies
import io.reactivex.Single

interface ICurrenciesRepository {
    fun getCurrencies(): Single<Result<Currencies>>
}