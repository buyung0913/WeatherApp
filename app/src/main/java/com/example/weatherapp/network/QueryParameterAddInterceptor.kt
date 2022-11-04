package com.example.weatherapp.network

import com.example.weatherapp.App
import com.example.weatherapp.util.APP_ID
import com.example.weatherapp.util.PrefManager
import okhttp3.Interceptor
import okhttp3.Response

class QueryParameterAddInterceptor : Interceptor {

    val context = App.context
    private val prefManager = PrefManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url().newBuilder()
            .addQueryParameter("appid", APP_ID)
            .addQueryParameter("units", prefManager.tempUnit)
            .build()

        val request = chain.request().newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}