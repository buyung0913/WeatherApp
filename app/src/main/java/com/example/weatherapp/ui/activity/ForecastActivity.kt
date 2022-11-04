package com.example.weatherapp.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapp.R
import com.example.weatherapp.data.repository.remote.WeatherRepository
import com.example.weatherapp.ui.adapter.ForecastAdapter
import com.example.weatherapp.util.Status
import com.example.weatherapp.util.lightStatusBar
import com.example.weatherapp.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.activity_forecast.*
import kotlinx.android.synthetic.main.layout_toolbar.*

class ForecastActivity : AppCompatActivity() {
    private lateinit var viewModel: AppViewModel
    private lateinit var repository: WeatherRepository
    private lateinit var mAdapter: ForecastAdapter
    private var lat: String? = null
    private var lon: String? = null
    private var city: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(android.R.color.white)
        lightStatusBar(this, true)
        setContentView(R.layout.activity_forecast)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]
        repository = WeatherRepository()
        mAdapter = ForecastAdapter()
        lat = intent.getStringExtra(LATITUDE)
        lon = intent.getStringExtra(LONGITUDE)
        city = intent.getStringExtra(CITY_NAME)

        txt_tool_title.text = city
        if (lat != null && lon != null) viewModel.getWeatherForecast(
            repository,
            lat!!,
            lon!!,
            EXCLUDE
        )

        setUpRecyclerView()
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.weatherForecast.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        loading.visibility = View.GONE
                        container_error.visibility = View.GONE
                        rv_forecast.visibility = View.VISIBLE
                        mAdapter.differ.submitList(it.data?.daily)
                    }

                    Status.ERROR -> {
                        showFailedView(it.message)
                    }

                    Status.LOADING -> {
                        loading.visibility = View.VISIBLE
                        container_error.visibility = View.GONE
                        rv_forecast.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showFailedView(message: String?) {
        container_error.visibility = View.VISIBLE
        loading.visibility = View.GONE
        rv_forecast.visibility = View.GONE
    }

    private fun setUpRecyclerView() {
        rv_forecast.apply {
            layoutManager = LinearLayoutManager(this@ForecastActivity)
            setHasFixedSize(true)
            adapter = mAdapter
        }
    }

    fun onBackButtonClicked2(view: View) {
        onBackPressed()
        finish()
    }

    fun onForecastRetryButtonClicked(view: View) {
        setUpObservers()
    }

    companion object {
        const val LATITUDE = "lat"
        const val LONGITUDE = "lon"
        const val CITY_NAME = "city"
        const val EXCLUDE = "current,minutely,hourly"
    }
}