package com.revolut.finance.data.source.webservice.deserializer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.revolut.finance.data.source.webservice.dto.CurrenciesResponse
import com.revolut.finance.data.source.webservice.dto.RateResponse
import java.lang.reflect.Type

class CurrenciesResponseDeserializer : JsonDeserializer<CurrenciesResponse> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): CurrenciesResponse? {
        val jsonObject = json?.asJsonObject ?: return null
        val entries = jsonObject.entrySet()
        val rates = entries?.map { RateResponse(it.key, it.value.asDouble) }

        return CurrenciesResponse(jsonObject.get("base").asString, jsonObject.get("date").asString, rates)
    }
}