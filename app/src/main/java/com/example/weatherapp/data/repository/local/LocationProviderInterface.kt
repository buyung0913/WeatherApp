package com.example.weatherapp.data.repository.local

import com.example.weatherapp.data.model.LocationData
import com.example.weatherapp.util.RequestCompleteListener

interface LocationProviderInterface {
    fun getUserCurrentLocation(callback: RequestCompleteListener<LocationData>)
}