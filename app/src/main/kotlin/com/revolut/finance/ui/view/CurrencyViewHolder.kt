package com.revolut.finance.ui.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.blongho.country_data.World
import com.revolut.finance.ui.model.RateViewData
import kotlinx.android.synthetic.main.currency_rates_list_item.view.amount
import kotlinx.android.synthetic.main.currency_rates_list_item.view.code
import kotlinx.android.synthetic.main.currency_rates_list_item.view.displayName
import kotlinx.android.synthetic.main.currency_rates_list_item.view.flag

class CurrencyViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(data: RateViewData, onClickFunc: () -> Unit) {
        view.code.text = data.currency
        view.flag.setImageResource(World.getFlagOf(data.flag))
        view.displayName.text = data.currencyDisplayName
        view.amount.setText(data.amount)
        view.amount.setSelection(view.amount.text.length)

        view.setOnClickListener { onClickFunc() }
    }
}