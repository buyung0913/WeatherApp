package com.example.weatherapp.data.repository.remote

import com.example.weatherapp.network.RetrofitClient

class WeatherRepository {
    suspend fun getWeatherByLocation(lat: String, lon: String) =
        RetrofitClient.api.getWeatherByLocation(lat, lon)

    suspend fun getWeatherByCityID(id: String) = RetrofitClient.api.getWeatherByCityID(id)
    suspend fun getWeatherForecast(lat: String, lon: String, exclude: String) =
        RetrofitClient.api.getWeatherForecast(lat, lon, exclude)

    suspend fun getWeatherByCityName(q: String) =
        RetrofitClient.api.getWeatherByCity(q)
}