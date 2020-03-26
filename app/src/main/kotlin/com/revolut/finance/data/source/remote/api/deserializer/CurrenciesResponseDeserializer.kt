package com.revolut.finance.data.source.remote.api.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import com.revolut.finance.data.source.remote.api.dto.RateResponse
import java.lang.reflect.Type

private const val RATES_KEY = "rates"
private const val BASE_KEY = "baseCurrency"

class CurrenciesResponseDeserializer : JsonDeserializer<CurrenciesResponse> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CurrenciesResponse? {
        val jsonObject = json?.asJsonObject ?: return null
        val ratesJson = jsonObject.get(RATES_KEY)
        val rates = ratesJson.asJsonObject.entrySet().map { RateResponse(it.key, it.value.asDouble) }

        return CurrenciesResponse(jsonObject.get(BASE_KEY).asString, rates)
    }
}