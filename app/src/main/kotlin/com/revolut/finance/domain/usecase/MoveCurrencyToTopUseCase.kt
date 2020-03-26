package com.revolut.finance.domain.usecase

import com.revolut.currencies.R
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.sortWithExistingTop
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import io.reactivex.Observable

class MoveCurrencyToTopUseCase(
    private val repository: ICurrenciesRepository,
    private val mapper: Mapper<Currencies, CurrenciesViewData>
) : UseCase<String, UIResult<CurrenciesViewData>>() {

    override fun run(input: String): Observable<UIResult<CurrenciesViewData>> =
        Observable.fromCallable { moveCurrencyTop(input) }
            .onErrorReturnItem(UIResult.Error(R.string.move_currency_to_top_error_message))

    private fun moveCurrencyTop(currency: String): UIResult<CurrenciesViewData> {
        repository.setTopCurrency(currency)
        return UIResult.Success(mapper.mapTo(repository.getCurrenciesFromCache().sortWithExistingTop(repository.getTopCurrency())!!))
    }
}