package com.example.weatherapp.data.model

import com.google.gson.annotations.SerializedName

data class ResponseWeatherForecast(
    val daily: List<Daily>,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int
)

data class Daily(
    val clouds: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val dt: Int,
    @SerializedName("feels_like")
    val feelsLike: FeelsLike,
    val humidity: Int,
    val pop: Double,
    val pressure: Int,
    val rain: Double,
    val sunrise: Int,
    val sunset: Int,
    val temp: Temp,
    val uvi: Double,
    val weather: List<WeatherF>,
    @SerializedName("wind_deg")
    val windDeg: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double
)

data class FeelsLike(
    val day: Double,
    val eve: Double,
    val morn: Double,
    val night: Double
)

data class Temp(
    val day: Double,
    val eve: Double,
    val max: Double,
    val min: Double,
    val morn: Double,
    val night: Double
)

data class WeatherF(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
)