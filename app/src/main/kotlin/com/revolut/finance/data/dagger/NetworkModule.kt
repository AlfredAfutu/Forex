package com.revolut.finance.data.dagger

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.revolut.finance.data.source.remote.api.deserializer.CurrenciesResponseDeserializer
import com.revolut.finance.data.source.remote.api.dto.CurrenciesResponse
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object NetworkModule {
    private val baseUrl: String
        get() = "https://hiring.revolut.codes/"

    @Provides
    @Singleton
    fun provideGson(currenciesResponseDeserializer: JsonDeserializer<CurrenciesResponse>): Gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(CurrenciesResponse::class.java, currenciesResponseDeserializer)
            .create()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, httpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideResponseDeserializer(): JsonDeserializer<CurrenciesResponse> = CurrenciesResponseDeserializer()

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        return httpClient.addInterceptor(logging).build()
    }
}