package com.example.weatherapp.util

interface RequestCompleteListener<T> {
    fun onRequestCompleted(data: T)
    fun onRequestFailed(errorMessage: String?)
}