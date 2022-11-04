package com.example.weatherapp.data.repository.local

import com.example.weatherapp.data.model.Cities
import com.example.weatherapp.data.model.CityUpdate
import com.example.weatherapp.db.CityDatabase

class CityRepository(
    private val database: CityDatabase
) {
    suspend fun searchCities(key: String) = database.getCityDao().searchCity(key)
    suspend fun updateSavedCities(obj: CityUpdate) = database.getCityDao().updateSavedCity(obj)
    fun getSavedCities(key: Int) = database.getCityDao().getSavedCity(key)
    suspend fun insertCity(cities: Cities) = database.getCityDao().insertCity(cities)
    suspend fun deleteSavedCities(cities: Cities) = database.getCityDao().deleteSavedCity(cities)
}