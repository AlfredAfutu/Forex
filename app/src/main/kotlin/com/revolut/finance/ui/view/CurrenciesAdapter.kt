package com.revolut.finance.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencies.R
import com.revolut.finance.ui.model.RateViewData

class CurrenciesAdapter(private val onRateItemClickedListener: (String) -> Unit) : RecyclerView.Adapter<CurrencyViewHolder>() {

    private val rates: MutableList<RateViewData> = mutableListOf()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder =
        CurrencyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.currency_rates_list_item, parent, false))

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) =
        holder.bind(rates[position]) { onRateItemClicked(rates[position].currency) }

    private fun onRateItemClicked(currency: String) = onRateItemClickedListener(currency)

    override fun getItemCount(): Int = rates.size

    override fun getItemId(position: Int): Long = position.toLong()

    fun setData(rates: List<RateViewData>) {
        this.rates.clear()
        this.rates.addAll(rates)
    }

    fun updateData(rates: List<RateViewData>) {
        this.rates.forEachIndexed { index, _ -> this.rates[index] = rates[index] }
    }
}