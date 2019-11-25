package com.revolut.finance.ui.currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencies.R
import com.revolut.finance.ui.currencies.model.RateViewData

class CurrenciesAdapter(private val onRateItemClickedListener: () -> Unit) : RecyclerView.Adapter<CurrencyViewHolder>() {

    private var rates: MutableList<RateViewData> = mutableListOf()

    private lateinit var topCurrency: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder(inflateView(parent, R.layout.currency_rates_list_item))

    private fun inflateView(parent: ViewGroup, @LayoutRes resourceId: Int) =
        LayoutInflater.from(parent.context).inflate(resourceId, parent, false)

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) = holder.bind(rates[position]) { onRateItemClicked(rates[position], position) }

    /**
     * On rate item clicked add item to top of list
     * and notify fragment via onRateItemClickedListener
     */
    private fun onRateItemClicked(rate: RateViewData, position: Int) {
        if (position != 0) {
            rates.run {
                removeAt(position)
                notifyItemRemoved(position)
                add(0, rate)
                notifyItemInserted(0)
            }
            topCurrency = rate.currency
        }
        onRateItemClickedListener()
    }

    override fun getItemCount(): Int = rates.size

    fun setData(rates: List<RateViewData>) {
        this.rates.clear()
        this.rates.addAll(rates)

        setTopCurrency()
    }

    /**
     * Check if there already exists a currency code at the top of the list
     * triggered by on click selection and make it the top item when rates
     * are updated
     */
    private fun setTopCurrency() {
        if (::topCurrency.isInitialized) {
            this.rates.run {
                val top = first { it.currency == topCurrency }
                remove(top)
                add(0, top)
            }
        }
    }
}