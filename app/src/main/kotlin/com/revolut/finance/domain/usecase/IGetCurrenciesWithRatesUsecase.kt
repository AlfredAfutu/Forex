package com.revolut.finance.domain.usecase

import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.currencies.model.CurrenciesViewData
import io.reactivex.Observable

interface IGetCurrenciesWithRatesUsecase {
    operator fun invoke(): Observable<UIResult<CurrenciesViewData>>
}