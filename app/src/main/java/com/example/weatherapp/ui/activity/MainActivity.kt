package com.example.weatherapp.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.data.model.Cities
import com.example.weatherapp.data.model.CityUpdate
import com.example.weatherapp.data.model.ResponseWeather
import com.example.weatherapp.data.repository.local.CityRepository
import com.example.weatherapp.data.repository.local.LocationProvider
import com.example.weatherapp.data.repository.remote.WeatherRepository
import com.example.weatherapp.db.CityDatabase
import com.example.weatherapp.util.*
import com.example.weatherapp.viewmodel.AppViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_additional_weather_info.*
import kotlinx.android.synthetic.main.layout_additional_weather_info.txt_sunrise_time
import kotlinx.android.synthetic.main.layout_additional_weather_info.txt_sunset_time
import kotlinx.android.synthetic.main.layout_forecast_info.*
import kotlinx.android.synthetic.main.layout_info.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: AppViewModel
    private lateinit var model: LocationProvider
    private lateinit var weatherRepo: WeatherRepository
    private lateinit var cityRepo: CityRepository
    private var isGPSEnabled = false
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
        setContentView(R.layout.activity_main)

        model = LocationProvider(this)
        viewModel = ViewModelProvider(this)[AppViewModel::class.java]
        weatherRepo = WeatherRepository()
        cityRepo = CityRepository(CityDatabase(this))

        //checking GPS status
        GpsUtils(this).turnGPSOn(object : GpsUtils.OnGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                this@MainActivity.isGPSEnabled = isGPSEnable
            }
        })

        setUpObservers()
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }

    private fun setUpObservers() {
        viewModel.locationLiveData.observe(this) {
            viewModel.getWeatherByLocation(
                weatherRepo,
                it.latitude.toString(),
                it.longitude.toString()
            )
        }

        viewModel.weatherByLocation.observe(this) {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        inc_info_weather.visibility = View.VISIBLE
                        loading.visibility = View.GONE
                        container_error.visibility = View.GONE
                        setUpUI(it.data)
                    }

                    Status.ERROR -> {
                        showFailedView(it.message)
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
        loading.visibility = View.GONE
        inc_info_weather.visibility = View.GONE

        when (message) {
            "Network Failure" -> {
                container_error.visibility = View.VISIBLE
            }
            else -> {
                container_error.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setUpUI(data: ResponseWeather?) {
        id = data?.id
        lonD = data!!.coord.lon
        latD = data!!.coord.lat
        img_back.visibility = View.GONE
        img_search.visibility = View.VISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }

    private fun invokeLocationAction() {
        when {
            !isGPSEnabled -> showToast(this, "Enable GPS", 1)

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() -> requestLocationPermission()

            else -> requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST
        )
    }

    private fun startLocationUpdate() {
        viewModel.getCurrentLocation(model)
    }

    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
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

    fun onAddButtonClicked(view: View) {
        startActivity(Intent(this@MainActivity, SavedCityActivity::class.java))
    }

    fun onForecastButtonClicked(view: View) {
        startActivity(
            Intent(this@MainActivity, ForecastActivity::class.java)
                .putExtra(ForecastActivity.LATITUDE, lat)
                .putExtra(ForecastActivity.LONGITUDE, lon)
                .putExtra(ForecastActivity.CITY_NAME, city)
        )
    }

    fun onWeatherRetryButtonClicked(view: View) {
        setUpObservers()
    }
}