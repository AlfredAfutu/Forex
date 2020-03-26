package com.revolut.finance.ui.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.widget.RxTextView
import com.revolut.currencies.R
import com.revolut.finance.ui.extension.closeKeyboard
import com.revolut.finance.ui.extension.showKeyboard
import com.revolut.finance.ui.model.RateViewData
import com.revolut.finance.ui.viewmodel.CurrenciesViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.currencies_fragment.currencyRates
import kotlinx.android.synthetic.main.currencies_fragment.emptyList
import kotlinx.android.synthetic.main.currencies_fragment.progress
import kotlinx.android.synthetic.main.currency_rates_list_item.view.amount
import javax.inject.Inject

private const val SCROLL_Y_THRESHOLD = 10

class CurrenciesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val currenciesAdapter by lazy { CurrenciesAdapter { onRateItemClicked(it) } }

    private val viewModel: CurrenciesViewModel by viewModels { viewModelFactory }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.currencies_fragment, container, false)

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setEmptyListClickListener()

        viewModel.fetchCurrencies()
        observeCurrenciesWithRates()
        observeUpdatedRates()
        observeCurrencyMovedToTop()
        observeListViewScroll()
        observeListScrolled()
        observeErrorMessage()
        observeFetchCurrencyError()
    }

    private fun setEmptyListClickListener() {
        emptyList.setOnClickListener {
            it.visibility = GONE
            progress.visibility = VISIBLE
            viewModel.fetchCurrencies()
        }
    }

    private fun observeCurrenciesWithRates() {
        viewModel.getCurrenciesWithRates().observe(this, Observer {
            progress.visibility = GONE
            emptyList.visibility = GONE
            currencyRates.visibility = VISIBLE
            updateList(it.rates)
        })
    }

    private fun observeUpdatedRates() {
        viewModel.getUpdatedRates().observe(this, Observer {
            updateRates(it)
        })
    }

    private fun observeCurrencyMovedToTop() {
        viewModel.getCurrencyMovedToTop().observe(this, Observer { currency ->
            val amountEditText = currencyRates.layoutManager?.findViewByPosition(0)?.amount
            amountEditText?.let {
                if (it.requestFocus()) activity?.showKeyboard(it)
                viewModel.observeTopCurrencyAmountChangeEvents(RxTextView.textChanges(it).skipInitialValue(), currency)
            }
        })
    }

    private fun observeListViewScroll() {
        viewModel.observeListViewScroll(RxRecyclerView.scrollEvents(currencyRates).map { it.dy() }.filter { scrollY -> scrollY > SCROLL_Y_THRESHOLD })
    }

    private fun observeListScrolled() {
        viewModel.isListScrolled().observe(this, Observer { activity?.closeKeyboard() })
    }

    private fun observeErrorMessage() {
        viewModel.getErrorMessage().observe(this, Observer {
            Snackbar.make(activity?.findViewById(android.R.id.content)!!, getText(it), Snackbar.LENGTH_SHORT).show()
        })
    }

    private fun observeFetchCurrencyError() {
        viewModel.isFetchCurrencyError().observe(this, Observer {
            progress.visibility = GONE
            currencyRates.visibility = GONE
            emptyList.visibility = VISIBLE
        })
    }

    private fun updateList(rates: List<RateViewData>) {
        currenciesAdapter.setData(rates)
        currenciesAdapter.notifyDataSetChanged()
    }

    private fun updateRates(rates: List<RateViewData>) {
        currenciesAdapter.updateData(rates)
        currenciesAdapter.notifyItemRangeChanged(1, rates.size)
    }

    private fun setupRecyclerView() {
        currencyRates.adapter = currenciesAdapter
    }

    private fun onRateItemClicked(currency: String) {
        viewModel.moveCurrencyToTop(currency)
        currencyRates.layoutManager?.scrollToPosition(0)
    }
}