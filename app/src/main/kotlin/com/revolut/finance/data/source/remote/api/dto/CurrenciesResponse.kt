package com.revolut.finance.data.source.remote.api.dto

import com.google.gson.annotations.SerializedName

data class CurrenciesResponse(
    @SerializedName("baseCurrency")
    val base: String? = null,
    val rates: List<RateResponse>? = null
)

data class RateResponse(val currency: String, val amount: Double?)