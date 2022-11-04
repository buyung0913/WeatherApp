package com.example.weatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.*
import com.example.weatherapp.data.repository.local.CityRepository
import com.example.weatherapp.data.repository.local.LocationProvider
import com.example.weatherapp.data.repository.remote.WeatherRepository
import com.example.weatherapp.util.RequestCompleteListener
import com.example.weatherapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class AppViewModel : ViewModel() {
    private val tag = "ViewModel"

    //location live data
    val locationLiveData = MutableLiveData<LocationData>()
    val locationLiveDataFailure = MutableLiveData<String>()

    //weatherByLocation live data
    val weatherByLocation = MutableLiveData<Resource<ResponseWeather>>()

    //cityBySearch live data
    val cityByQuery = MutableLiveData<Resource<List<Cities>>>()

    //weatherByCityID live data
    val weatherByCityID = MutableLiveData<Resource<ResponseWeather>>()

    //weatherForecast live data
    val weatherForecast = MutableLiveData<Resource<ResponseWeatherForecast>>()

    //weatherByCityName live data
    val weatherByCityName = MutableLiveData<Resource<ResponseWeather>>()

    fun getCurrentLocation(model: LocationProvider) {
        model.getUserCurrentLocation(object : RequestCompleteListener<LocationData> {
            override fun onRequestCompleted(data: LocationData) {
                locationLiveData.postValue(data)
            }

            override fun onRequestFailed(errorMessage: String?) {
                locationLiveDataFailure.postValue(errorMessage)
            }
        })
    }

    /**
     * Weather by Location call
     */
    fun getWeatherByLocation(model: WeatherRepository, lat: String, lon: String) {
        viewModelScope.launch { safeWeatherByLocationFetch(model, lat, lon) }
    }

    private suspend fun safeWeatherByLocationFetch(
        model: WeatherRepository, lat: String, lon: String
    ) {
        weatherByLocation.postValue(Resource.loading(null))
        try {
            val response = model.getWeatherByLocation(lat, lon)
            weatherByLocation.postValue(handleWeatherResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherByLocation.postValue(
                    Resource.error(
                        null, "Network Failure"
                    )
                )
                else -> weatherByLocation.postValue(Resource.error(null, t.localizedMessage))
            }
        }
    }

    /**
     * Weather by CityName call
     */
    fun getWeatherByCityName(model: WeatherRepository, q: String) {
        viewModelScope.launch { safeWeatherByCityNameFetch(model, q) }
    }

    private suspend fun safeWeatherByCityNameFetch(
        model: WeatherRepository, q: String
    ) {
        weatherByCityName.postValue(Resource.loading(null))
        try {
            val response = model.getWeatherByCityName(q)
            weatherByCityName.postValue(handleWeatherResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherByCityName.postValue(
                    Resource.error(
                        null, "Network Failure"
                    )
                )
                else -> weatherByCityName.postValue(Resource.error(null, t.localizedMessage))
            }
        }
    }

    /**
     * Weather by CityID call
     */
    fun getWeatherByCityID(model: WeatherRepository, id: String) {
        viewModelScope.launch { safeWeatherByCityIDFetch(model, id) }
    }

    private suspend fun safeWeatherByCityIDFetch(model: WeatherRepository, id: String) {
        weatherByCityID.postValue(Resource.loading(null))
        try {
            val response = model.getWeatherByCityID(id)
            weatherByCityID.postValue(handleWeatherResponse(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherByCityID.postValue(Resource.error(null, "Network Failure"))
                else -> weatherByCityID.postValue(Resource.error(null, t.localizedMessage))
            }
        }
    }

    private fun handleWeatherResponse(response: Response<ResponseWeather>): Resource<ResponseWeather>? {
        return if (response.isSuccessful) Resource.success(response.body()) else Resource.error(
            null, "Error: ${response.errorBody()}"
        )
    }

    /**
     * Weather Forecast call
     */
    fun getWeatherForecast(model: WeatherRepository, lat: String, lon: String, exclude: String) {
        viewModelScope.launch { safeWeatherForecastFetch(model, lat, lon, exclude) }
    }

    private suspend fun safeWeatherForecastFetch(
        model: WeatherRepository, lat: String, lon: String, exclude: String
    ) {
        weatherForecast.postValue(Resource.loading(null))
        try {
            val response = model.getWeatherForecast(lat, lon, exclude)
            weatherForecast.postValue(handleWeatherForecast(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> weatherForecast.postValue(Resource.error(null, "Network Failure"))
                else -> weatherForecast.postValue(Resource.error(null, t.localizedMessage))
            }
        }
    }

    private fun handleWeatherForecast(response: Response<ResponseWeatherForecast>): Resource<ResponseWeatherForecast>? {
        return if (response.isSuccessful) Resource.success(response.body()) else Resource.error(
            null, "Error: ${response.errorBody()}"
        )
    }

    /**
     * City by query call
     */
    fun getCityByQuery(model: CityRepository, query: String) =
        viewModelScope.launch { safeCityByQueryFetch(model, query) }

    private suspend fun safeCityByQueryFetch(model: CityRepository, query: String) {
        cityByQuery.postValue(Resource.loading(null))
        try {
            val response = model.searchCities(key = query)
            cityByQuery.postValue(handleCitySearch(response))
        } catch (t: Throwable) {
            when (t) {
                is IOException -> cityByQuery.postValue(Resource.error(null, "Network Failure"))
                else -> {
                    cityByQuery.postValue(Resource.error(null, t.localizedMessage))
                    Log.e(tag, t.localizedMessage!!)
                }
            }
        }
    }

    private fun handleCitySearch(response: List<Cities>): Resource<List<Cities>>? =
        Resource.success(response)

    /**
     * Update City call
     */
    fun updateSavedCities(model: CityRepository, obj: CityUpdate) = viewModelScope.launch {
        try {
            val info = model.updateSavedCities(obj)
            Log.i(tag, "Success: Updating City DB: $info")
        } catch (e: Exception) {
            e.stackTrace
            Log.e(tag, "Error: Updating City DB: ${e.localizedMessage}")
        }
    }

    /**
     * Update City call
     */
    fun insertCity(model: CityRepository, cities: Cities) = viewModelScope.launch {
        try {
            model.insertCity(cities)
        } catch (e: Exception) {
            e.stackTrace
            Log.e(tag, "Error: Insert City DB: ${e.localizedMessage}")
        }
    }

    /**
     * Saved City call
     */
    fun getSavedCities(model: CityRepository, key: Int) = model.getSavedCities(key)

    /**
     * Delete City call
     */
    fun deleteSavedCities(model: CityRepository, cities: Cities) = viewModelScope.launch {
        try {
            model.deleteSavedCities(cities)
        } catch (e: Exception) {
            e.stackTrace
            Log.e(tag, "Error: Deleting City DB: ${e.localizedMessage}")
        }
    }
}