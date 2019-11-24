package com.revolut.finance.ui.currencies

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.revolut.finance.domain.usecase.IGetCurrenciesWithRatesUsecase
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.currencies.model.CurrenciesViewData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class CurrenciesViewModel @Inject constructor(private val getCurrenciesWithRatesInteractor: IGetCurrenciesWithRatesUsecase) : ViewModel() {

    private val currenciesWithRates = MutableLiveData<CurrenciesViewData>()

    private val errorMessage = MutableLiveData<String>()

    private val currenciesLoading = MutableLiveData<Boolean>()

    private val disposable = CompositeDisposable()

    fun getCurrenciesWithRates(): LiveData<CurrenciesViewData> = currenciesWithRates

    fun getErrorMessage(): LiveData<String> = errorMessage

    fun isCurrenciesLoading(): LiveData<Boolean> = currenciesLoading

    fun fetchCurrencies() {
        disposable.clear()

        disposable.add(
            getCurrenciesWithRatesInteractor()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { currenciesLoading.value = true }
                .doOnNext { currenciesLoading.value = false }
                .doOnError { currenciesLoading.value = false }
                .subscribe(::handleFetchCurrenciesResult)
        )
    }

    private fun handleFetchCurrenciesResult(result: UIResult<CurrenciesViewData>) {
        when (result) {
            is UIResult.Success -> println("Successful fetch: ${result.data}")
            is UIResult.Error -> println("Error in fetch")
        }
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}
