package com.example.weatherapp.util

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.data.model.Cities
import com.example.weatherapp.data.model.Daily

class DiffUtilCallback : DiffUtil.ItemCallback<Cities>() {
    override fun areItemsTheSame(oldItem: Cities, newItem: Cities): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Cities, newItem: Cities): Boolean {
        return oldItem == newItem
    }
}

class DiffUtilCallbackForecast : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }
}