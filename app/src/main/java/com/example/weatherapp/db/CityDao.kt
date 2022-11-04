package com.example.weatherapp.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weatherapp.data.model.Cities
import com.example.weatherapp.data.model.CityUpdate

@Dao
interface CityDao {
    @Query("SELECT * FROM city_db WHERE name LIKE :key || '%'")
    suspend fun searchCity(key: String): List<Cities>

    @Update(entity = Cities::class)
    suspend fun updateSavedCity(vararg obj: CityUpdate): Int

    @Query("SELECT * FROM city_db WHERE id= :key")
    fun getSavedCity(key: Int): LiveData<List<Cities>>

    @Insert
    suspend fun insertCity(city: Cities)

    @Delete
    suspend fun deleteSavedCity(city: Cities)
}