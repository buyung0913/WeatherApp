package com.example.weatherapp.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Cities
import com.example.weatherapp.data.model.CityUpdate
import com.example.weatherapp.data.model.ResponseWeather
import com.example.weatherapp.data.repository.local.CityRepository
import com.example.weatherapp.data.repository.remote.WeatherRepository
import com.example.weatherapp.util.Status
import com.example.weatherapp.util.lightStatusBar
import com.example.weatherapp.util.unixTimestampToTimeString
import com.example.weatherapp.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.activity_weather_details.*
import kotlinx.android.synthetic.main.layout_additional_weather_info.*
import kotlinx.android.synthetic.main.layout_info.*

class WeatherDetailsActivity : AppCompatActivity() {
    private lateinit var viewModel: AppViewModel
    private lateinit var weatherRepo: WeatherRepository
    private lateinit var cityRepo: CityRepository
    private var id: Int? = null
    private var latD: Double? = null
    private var lonD: Double? = null
    private var lat: String? = null
    private var lon: String? = null
    private var city: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(android.R.color.white)
        lightStatusBar(this, true)
        setContentView(R.layout.activity_weather_details)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]
        weatherRepo = WeatherRepository()
        city = intent.getStringExtra(CITY)

//        if (lat != null && lon != null) viewModel.getWeatherByCityName(
//            weatherRepo,
//            intent.getStringExtra(CITY).toString()
//        )
        viewModel.getWeatherByCityName(weatherRepo, intent.getStringExtra(CITY).toString())
        setUpObservers()
    }

    private fun setUpObservers() {
        viewModel.locationLiveData.observe(this) {
            viewModel.getWeatherByLocation(
                weatherRepo,
                it.latitude.toString(),
                it.longitude.toString()
            )
        }

        viewModel.weatherByCityName.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        inc_info_weather.visibility = View.VISIBLE
                        loading.visibility = View.GONE
                        container_error.visibility = View.GONE
                        setUpUI(it.data)
//                        viewModel.updateSavedCities(cityRepo, CityUpdate(it.data?.id, 1))
                    }

                    Status.ERROR -> {
                        showFailedView("City not found!!")
                    }

                    Status.LOADING -> {
                        loading.visibility = View.VISIBLE
                        container_error.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun showFailedView(message: String?) {
        container_error.visibility = View.VISIBLE
        loading.visibility = View.GONE
        inc_info_weather.visibility = View.GONE
        txt_error.text = message
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUI(data: ResponseWeather?) {
        img_back.visibility = View.VISIBLE
        img_search.visibility = View.GONE
        txt_temp.text = data?.main?.temp.toString()
        txt_city_name.text = data?.name
        txt_weather_condition.text = data?.weather!![0].main
        txt_sunrise_time.text = data.sys.sunrise.unixTimestampToTimeString()
        txt_sunset_time.text = data.sys.sunset.unixTimestampToTimeString()
        txt_real_feel_text.text =
            "${data.main.feelsLike}${getString(R.string.degree_celsius_symbol)}"
        txt_cloudiness_text.text = "${data.clouds.all}%"
        txt_wind_speed_text.text = "${data.wind.speed}m/s"
        txt_humidity_text.text = "${data.main.humidity}%"
        txt_pressure_text.text = "${data.main.pressure}hPa"
        txt_visibility_text.text = "${data.visibility}M"

        lat = data.coord.lat.toString()
        lon = data.coord.lon.toString()
        city = data.name
    }

    fun onResultRetryButtonClicked(view: View) {
        setUpObservers()
    }

    fun onForecastButtonClicked(view: View) {
        startActivity(
            Intent(this@WeatherDetailsActivity, ForecastActivity::class.java)
                .putExtra(ForecastActivity.LATITUDE, lat)
                .putExtra(ForecastActivity.LONGITUDE, lon)
                .putExtra(ForecastActivity.CITY_NAME, city)
        )
    }

    fun onInsertFavorite(view: View) {
        id?.let {
            viewModel.getSavedCities(cityRepo, it).observe(this) { city ->
                if (city[0].id != null) {
                    Toast.makeText(this, "Added to Favorite", Toast.LENGTH_LONG).show()
                    viewModel.insertCity(cityRepo, Cities(id, "", "", "", lonD, latD, 1))
                } else {
                    Toast.makeText(this, "Added to Favorite", Toast.LENGTH_LONG).show()
                    viewModel.updateSavedCities(cityRepo, CityUpdate(id, 1))
                }
            }
        }

        setUpObservers()
    }

    fun onBackButtonClicked(view: View) {
        onBackPressed()
        finish()
    }

    companion object {
        const val CITY = "city"
    }
}