package com.example.weatherapp.network

import com.example.weatherapp.util.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClient {
    companion object {
        private val retrofit by lazy {
            val httpClient = OkHttpClient
                .Builder()
                .addInterceptor(QueryParameterAddInterceptor())
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()

            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .client(httpClient)
                .build()
        }

        val api: ApiInterface by lazy {
            retrofit.create(ApiInterface::class.java)
        }
    }
}