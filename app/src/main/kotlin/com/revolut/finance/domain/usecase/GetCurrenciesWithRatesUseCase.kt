package com.revolut.finance.domain.usecase

import com.revolut.currencies.R
import com.revolut.finance.data.source.remote.api.ApiResult
import com.revolut.finance.domain.model.Currencies
import com.revolut.finance.domain.model.sortWithExistingTop
import com.revolut.finance.domain.repository.ICurrenciesRepository
import com.revolut.finance.mapper.Mapper
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GetCurrenciesWithRatesUseCase(
    private val repository: ICurrenciesRepository,
    private val mapper: Mapper<Currencies, CurrenciesViewData>
) : UseCase<Unit, UIResult<CurrenciesViewData>>() {

    override fun run(input: Unit): Observable<UIResult<CurrenciesViewData>> =
        Observable.interval(1, TimeUnit.SECONDS, Schedulers.computation())
            .flatMapSingle { repository.getCurrenciesFromApi() }
            .map { mapResult(it) }

    private fun mapResult(apiResult: ApiResult<Currencies>): UIResult<CurrenciesViewData> =
        when (apiResult) {
            is ApiResult.Success -> success(apiResult)
            is ApiResult.NetworkError -> UIResult.Error(R.string.network_error_message)
            else -> UIResult.Error(R.string.bad_response_message)
        }

    private fun success(apiResult: ApiResult.Success<Currencies>): UIResult.Success<CurrenciesViewData> {
        val apiCurrencies = apiResult.value ?: Currencies.EMPTY
        val sortedCurrencies = apiCurrencies.sortWithExistingTop(repository.getTopCurrency())
        return if (sortedCurrencies != null) {
            repository.setCacheCurrencies(sortedCurrencies)
            UIResult.Success(mapper.mapTo(repository.getCurrenciesFromCache()))
        } else {
            repository.setCacheCurrencies(apiCurrencies)
            UIResult.Success(mapper.mapTo(repository.getCurrenciesFromCache()))
        }
    }
}