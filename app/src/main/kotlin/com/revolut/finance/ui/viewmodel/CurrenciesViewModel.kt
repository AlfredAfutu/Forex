package com.revolut.finance.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revolut.currencies.R
import com.revolut.finance.domain.usecase.UseCase
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.model.RateViewData
import com.revolut.finance.ui.model.UpdateRatesData
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CurrenciesViewModel @Inject constructor(
    private val getCurrenciesWithRates: UseCase<Unit, UIResult<CurrenciesViewData>>,
    private val moveCurrencyTop: UseCase<String, UIResult<CurrenciesViewData>>,
    private val updateRates: UseCase<UpdateRatesData, UIResult<List<RateViewData>>>
) : ViewModel() {

    private val currenciesWithRates = MutableLiveData<CurrenciesViewData>()

    private val errorMessage = MutableLiveData<@androidx.annotation.StringRes Int>()

    private val fetchCurrencyError = MutableLiveData<Boolean>()

    private val listScrolled = MutableLiveData<Boolean>()

    private val currencyMovedToTop = MutableLiveData<String>()

    private val updatedRates = MutableLiveData<List<RateViewData>>()

    private val disposable = CompositeDisposable()

    private val textChangeDisposable = CompositeDisposable()

    private val fetchCurrenciesDisposable = CompositeDisposable()

    fun getCurrenciesWithRates(): LiveData<CurrenciesViewData> = currenciesWithRates

    fun getErrorMessage(): LiveData<Int> = errorMessage

    fun isListScrolled(): LiveData<Boolean> = listScrolled

    fun isFetchCurrencyError(): LiveData<Boolean> = fetchCurrencyError

    fun getUpdatedRates(): LiveData<List<RateViewData>> = updatedRates

    fun getCurrencyMovedToTop(): LiveData<String> = currencyMovedToTop

    fun fetchCurrencies() {
        fetchCurrenciesDisposable.clear()

        fetchCurrenciesDisposable.add(
            getCurrenciesWithRates(Unit)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext { if (it is UIResult.Error) sideEffectFetchCurrenciesError() }
                .subscribe(::handleFetchCurrenciesResult) {
                    fetchCurrencyError.value = true
                    errorMessage.value = R.string.general_error_message
                }
        )
    }

    private fun sideEffectFetchCurrenciesError() {
        fetchCurrencyError.value = true
        fetchCurrenciesDisposable.clear()
    }

    fun moveCurrencyToTop(currency: String) {
        disposable.add(
            moveCurrencyTop(currency)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { currencyMovedToTop.value = currency }
                .subscribe(::handleFetchCurrenciesResult) { errorMessage.value = R.string.general_error_message }
        )
    }

    private fun handleFetchCurrenciesResult(result: UIResult<CurrenciesViewData>) {
        when (result) {
            is UIResult.Success -> currenciesWithRates.value = result.data
            is UIResult.Error -> errorMessage.value = result.errorMessage
        }
    }

    fun observeTopCurrencyAmountChangeEvents(textChangedObservable: Observable<CharSequence>, currency: String) {
        fetchCurrencies()
        textChangeDisposable.clear()

        textChangeDisposable.add(
            textChangedObservable
                .skipWhile { it.toString() == currenciesWithRates.value?.rates!![0].amount }
                .doOnNext { fetchCurrenciesDisposable.clear() }
                .map { it.toString() }
                .map { UpdateRatesData(it, currency) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .flatMap { updateRates(it) }
                .doFinally { fetchCurrencies() }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(::handleUpdatedRatesResult) { errorMessage.value = R.string.general_error_message }
        )
    }

    private fun handleUpdatedRatesResult(result: UIResult<List<RateViewData>>) {
        when (result) {
            is UIResult.Success -> updatedRates.value = result.data
            is UIResult.Error -> errorMessage.value = result.errorMessage
        }
    }

    override fun onCleared() {
        fetchCurrenciesDisposable.clear()
        textChangeDisposable.clear()
        disposable.clear()
        super.onCleared()
    }

    fun observeListViewScroll(scrollEventObservable: Observable<Int>) {
        disposable.add(
            scrollEventObservable
                .doOnNext { textChangeDisposable.clear().also { listScrolled.value = true } }
                .subscribe({}, { println("Error on list scrolled $it") })
        )
    }
}
