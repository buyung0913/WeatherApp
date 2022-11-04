package com.example.weatherapp.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.R
import com.example.weatherapp.util.lightStatusBar
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(android.R.color.white)
        lightStatusBar(this@SearchActivity, true)
        window.navigationBarColor = resources.getColor(android.R.color.white)
        setContentView(R.layout.activity_search)
    }

    fun onSearchButtonClicked(view: View) {
        startActivity(
            Intent(this@SearchActivity, WeatherDetailsActivity::class.java).putExtra(
                WeatherDetailsActivity.CITY,
                txt_search_city.text.toString()
            )
        )
    }

    fun onCancelButtonClicked(view: View) {
        navigateBack()
    }

    private fun navigateBack() {
        val intent = Intent(this@SearchActivity, SavedCityActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair.create(txt_search_city, getString(R.string.label_search_hint))
        )
        startActivity(intent, options.toBundle())
        Handler(Looper.myLooper()!!).postDelayed({ finish() }, 1000)
    }

    override fun onBackPressed() {
        navigateBack()
    }
}