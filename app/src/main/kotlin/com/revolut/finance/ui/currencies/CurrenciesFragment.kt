package com.revolut.finance.ui.currencies

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.revolut.currencies.R
import com.revolut.finance.ui.currencies.model.RateViewData
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.currencies_fragment.*
import kotlinx.android.synthetic.main.currency_rates_list_item.view.*
import javax.inject.Inject

private const val REQUEST_FOCUS_DELAY = 500L

class CurrenciesFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val currenciesAdapter by lazy { CurrenciesAdapter { onRateItemClicked() } }

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

        viewModel.fetchCurrencies()

        viewModel.getCurrenciesWithRates().observe(this, Observer {
            progress.visibility = GONE
            currencyRates.visibility = VISIBLE
            updateList(it.rates)
        })
    }

    private fun updateList(rates: List<RateViewData>) {
        currenciesAdapter.setData(rates)
        currenciesAdapter.notifyDataSetChanged()
    }

    private fun setupRecyclerView() {
        currencyRates.adapter = currenciesAdapter
    }

    private fun onRateItemClicked() {
        currencyRates.layoutManager?.scrollToPosition(0)
        Handler().postDelayed({
            val amountEditText = currencyRates.layoutManager?.findViewByPosition(0)?.amount
            amountEditText?.requestFocus()
        }, REQUEST_FOCUS_DELAY)
    }
}
