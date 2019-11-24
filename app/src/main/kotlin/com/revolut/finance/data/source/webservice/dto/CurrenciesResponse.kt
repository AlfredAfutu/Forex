package com.revolut.finance.data.source.webservice.dto

import com.google.gson.annotations.SerializedName

data class CurrenciesResponse(
    @SerializedName("base")
    val base: String? = null,
    @SerializedName("date")
    val date: String? = null,
    val rates: List<RateResponse>? = null
)

data class RateResponse(val currency: String, val amount: Double)