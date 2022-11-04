package com.example.weatherapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CityUpdate(
    @ColumnInfo(name = "id")
    var id: Int? = null,

    @ColumnInfo(name = "isSaved")
    var isSaved: Int? = null
)
