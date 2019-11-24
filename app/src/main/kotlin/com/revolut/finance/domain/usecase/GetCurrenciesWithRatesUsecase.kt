package com.revolut.finance.domain.usecase

import com.revolut.currencies.R
import com.revolut.finance.data.repository.ICurrenciesRepository
import com.revolut.finance.data.source.webservice.api.Result
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.currencies.model.CurrenciesViewData
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GetCurrenciesWithRatesUsecase(
    private val repository: ICurrenciesRepository,
    private val mapper: Mapper<Currencies, CurrenciesViewData>
) : IGetCurrenciesWithRatesUsecase {

    override fun invoke(): Observable<UIResult<CurrenciesViewData>> {
        return Observable.interval(1, TimeUnit.SECONDS)
            .flatMapSingle { repository.getCurrencies() }
            .distinctUntilChanged()
            .subscribeOn(Schedulers.io())
            .map { mapResult(it) }
    }

    private fun mapResult(result: Result<Currencies>): UIResult<CurrenciesViewData> =
        when (result) {
            is Result.Success -> UIResult.Success(mapper.mapTo(result.value))
            is Result.NetworkError -> UIResult.Error(R.string.network_error_message)
            else -> UIResult.Error(R.string.bad_response_message)
        }
}