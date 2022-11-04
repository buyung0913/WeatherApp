package com.example.weatherapp.network

import com.example.weatherapp.data.model.ResponseWeather
import com.example.weatherapp.data.model.ResponseWeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("weather")
    suspend fun getWeatherByLocation(
        @Query("lat")
        latitude: String,
        @Query("lon")
        longitude: String
    ): Response<ResponseWeather>

    @GET("weather")
    suspend fun getWeatherByCityID(
        @Query("id")
        query: String
    ): Response<ResponseWeather>

    @GET("onecall")
    suspend fun getWeatherForecast(
        @Query("lat")
        latitude: String,
        @Query("lon")
        longitude: String,
        @Query("exclude")
        exclude: String
    ): Response<ResponseWeatherForecast>

    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q")
        cityName: String
    ): Response<ResponseWeather>
}