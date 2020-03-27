package com.revolut.finance.ui.viewmodel

import com.jraska.livedata.test
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import com.revolut.currencies.R
import com.revolut.finance.BaseTest
import com.revolut.finance.domain.usecase.UseCase
import com.revolut.finance.ui.UIResult
import com.revolut.finance.ui.model.CurrenciesViewData
import com.revolut.finance.ui.model.RateViewData
import com.revolut.finance.ui.model.UpdateRatesData
import io.reactivex.Observable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CurrenciesViewModelTest : BaseTest() {

    private val getCurrenciesWithRates: UseCase<Unit, UIResult<CurrenciesViewData>> = mock()
    private val moveCurrencyToTop: UseCase<String, UIResult<CurrenciesViewData>> = mock()
    private val updateRates: UseCase<UpdateRatesData, UIResult<List<RateViewData>>> = mock()

    private lateinit var viewModel: CurrenciesViewModel

    @BeforeEach
    override fun setup() {
        super.setup()
        viewModel = CurrenciesViewModel(getCurrenciesWithRates, moveCurrencyToTop, updateRates)

        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.never())
        whenever(moveCurrencyToTop(any())).thenReturn(Observable.never())
        whenever(updateRates(any())).thenReturn(Observable.never())
    }

    @Test
    fun `when fetchCurrencies is called, invoke getCurrenciesWithRates`() {
        viewModel.fetchCurrencies()

        verify(getCurrenciesWithRates)(Unit)
    }

    @Test
    fun `when getCurrenciesWithRates emits a UIResult with error, show error message`() {
        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Error(R.string.network_error_message)))

        viewModel.fetchCurrencies()

        triggerActions()

        viewModel.getErrorMessage().test().assertValue(R.string.network_error_message)
        viewModel.isFetchCurrencyError().test().assertValue(true)
    }

    @Test
    fun `when getCurrenciesWithRates throws an error, show error message`() {
        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.error(RuntimeException()))

        viewModel.fetchCurrencies()

        triggerActions()

        viewModel.getErrorMessage().test().assertValue(R.string.general_error_message)
        viewModel.isFetchCurrencyError().test().assertValue(true)
    }

    @Test
    fun `when getCurrenciesWithRates emits a UIResult with success, show currencies`() {
        val viewData = CurrenciesViewData()
        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Success(viewData)))

        viewModel.fetchCurrencies()

        triggerActions()

        viewModel.getCurrenciesWithRates().test().assertValue(viewData)
    }

    @Test
    fun `when moveCurrencyToTop is called, invoke moveCurrencyToTop usecase`() {
        val currency = "USD"
        viewModel.moveCurrencyToTop(currency)

        verify(moveCurrencyToTop)(currency)
    }

    @Test
    fun `when moveCurrencyToTop emits a UIResult with error, show error message`() {
        val currency = "EUR"
        whenever(moveCurrencyToTop(currency)).thenReturn(Observable.just(UIResult.Error(R.string.move_currency_to_top_error_message)))

        viewModel.moveCurrencyToTop(currency)

        triggerActions()

        viewModel.getErrorMessage().test().assertValue(R.string.move_currency_to_top_error_message)
    }

    @Test
    fun `when moveCurrencyToTop throws an error, show error message`() {
        val currency = "EUR"
        whenever(moveCurrencyToTop(currency)).thenReturn(Observable.error(RuntimeException()))

        viewModel.moveCurrencyToTop(currency)

        triggerActions()

        viewModel.getErrorMessage().test().assertValue(R.string.general_error_message)
    }

    @Test
    fun `when moveCurrencyToTop emits a UIResult with success, show currencies`() {
        val viewData = CurrenciesViewData()
        val currency = "EUR"
        whenever(moveCurrencyToTop(currency)).thenReturn(Observable.just(UIResult.Success(viewData)))

        viewModel.moveCurrencyToTop(currency)

        triggerActions()

        viewModel.getCurrencyMovedToTop().test().assertValue(currency)
        viewModel.getCurrenciesWithRates().test().assertValue(viewData)
    }

    @Test
    fun `when observeTopCurrencyAmountChangeEvents is called, invoke getCurrenciesWithRates`() {
        val textChangeObservable: Observable<CharSequence> = Observable.just("43.54")
        val currency = "USD"

        viewModel.observeTopCurrencyAmountChangeEvents(textChangeObservable, currency)

        verify(getCurrenciesWithRates, times(2))(Unit)
    }

    @Test
    fun `when changed amount emitted by observable is same as rate amount at top of list, do not update rates`() {
        val amount = "43.54"
        val textChangeObservable: Observable<CharSequence> = Observable.just(amount)
        val currency = "EUR"
        val viewData = CurrenciesViewData(rates = listOf(RateViewData("EUR", "eu", amount, "Euro")))

        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Success(viewData)))

        viewModel.observeTopCurrencyAmountChangeEvents(textChangeObservable, currency)

        triggerActions()

        verifyZeroInteractions(updateRates)
    }

    @Test
    fun `when changed amount emitted by observable is not the same as rate amount at top of list, update rates`() {
        val amount = "43.54"
        val textChangeObservable: Observable<CharSequence> = Observable.just(amount)
        val currency = "EUR"
        val viewData = CurrenciesViewData(rates = listOf(RateViewData("EUR", "eu", "34.24", "Euro")))

        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Success(viewData)))

        viewModel.fetchCurrencies()

        triggerActions()

        viewModel.observeTopCurrencyAmountChangeEvents(textChangeObservable, currency)

        triggerActions()

        verify(updateRates)(UpdateRatesData(amount, currency))
    }

    @Test
    fun `when updateRates emits a UIResult with error, show update error message and fetch currencies`() {
        val amount = "43.54"
        val textChangeObservable: Observable<CharSequence> = Observable.just(amount)
        val currency = "EUR"
        val viewData = CurrenciesViewData(rates = listOf(RateViewData("EUR", "eu", "34.24", "Euro")))

        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Success(viewData)))
        whenever(updateRates(any())).thenReturn(Observable.just(UIResult.Error(R.string.rate_update_error_message)))

        viewModel.fetchCurrencies()

        triggerActions()

        viewModel.observeTopCurrencyAmountChangeEvents(textChangeObservable, currency)

        triggerActions()

        viewModel.getErrorMessage().test().assertValue(R.string.rate_update_error_message)
    }


    @Test
    fun `when updateRates emits an error thrown in the observable, show general error message and fetch currencies`() {
        val amount = "43.54"
        val textChangeObservable: Observable<CharSequence> = Observable.just(amount)
        val currency = "EUR"
        val viewData = CurrenciesViewData(rates = listOf(RateViewData("EUR", "eu", "34.24", "Euro")))

        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Success(viewData)))
        whenever(updateRates(any())).thenReturn(Observable.error(RuntimeException()))

        viewModel.observeTopCurrencyAmountChangeEvents(textChangeObservable, currency)

        triggerActions()

        verify(getCurrenciesWithRates, times(2))(Unit)
        viewModel.getErrorMessage().test().assertValue(R.string.general_error_message)
    }

    @Test
    fun `when updateRates emits a UIResult with success, show currencies`() {
        val amount = "43.54"
        val textChangeObservable: Observable<CharSequence> = Observable.just(amount)
        val currency = "EUR"
        val viewData = CurrenciesViewData(rates = listOf(RateViewData("EUR", "eu", "34.24", "Euro")))


        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Success(viewData)))
        whenever(updateRates(any())).thenReturn(Observable.just(UIResult.Success(viewData.rates)))

        viewModel.fetchCurrencies()

        triggerActions()

        viewModel.observeTopCurrencyAmountChangeEvents(textChangeObservable, currency)

        triggerActions()

        viewModel.getUpdatedRates().test().assertValue(viewData.rates)
    }

    @Test
    fun `when observeListViewScroll emits a value, dispose update rates fetch currencies and close keyboard`() {
        val amount = "43.54"
        val textChangeObservable: Observable<CharSequence> = Observable.just(amount)
        val currency = "EUR"
        val viewData = CurrenciesViewData(rates = listOf(RateViewData("EUR", "eu", "34.24", "Euro")))

        whenever(getCurrenciesWithRates(Unit)).thenReturn(Observable.just(UIResult.Success(viewData)))
        whenever(updateRates(any())).thenReturn(Observable.just(UIResult.Success(viewData.rates)))

        viewModel.observeTopCurrencyAmountChangeEvents(textChangeObservable, currency)

        viewModel.observeListViewScroll(Observable.just(2))

        triggerActions()

        verifyZeroInteractions(updateRates)
        verify(getCurrenciesWithRates, times(2))(Unit)
        viewModel.isListScrolled().test().assertValue(true)
    }
}