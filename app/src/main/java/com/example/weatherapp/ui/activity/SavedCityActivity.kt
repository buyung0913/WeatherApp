package com.example.weatherapp.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Pair
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.data.model.CityUpdate
import com.example.weatherapp.data.repository.local.CityRepository
import com.example.weatherapp.db.CityDatabase
import com.example.weatherapp.ui.adapter.SavedCityAdapter
import com.example.weatherapp.util.RecyclerItemTouchHelper
import com.example.weatherapp.util.lightStatusBar
import com.example.weatherapp.viewmodel.AppViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_saved_city.*

class SavedCityActivity : AppCompatActivity(),
    RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private lateinit var viewModel: AppViewModel
    private lateinit var repository: CityRepository
    private lateinit var mAdapter: SavedCityAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(android.R.color.white)
        lightStatusBar(this, true)
        setContentView(R.layout.activity_saved_city)

        viewModel = ViewModelProvider(this)[AppViewModel::class.java]
        repository = CityRepository(CityDatabase(this))
        mAdapter = SavedCityAdapter()

//        setUpRecyclerView()
//        setUpObservers()
    }

//    private fun setUpObservers() {
//        viewModel.getSavedCities(repository, 1).observe(this) { cities ->
//            mAdapter.differ.submitList(cities)
//        }
//    }
//
//    private fun setUpRecyclerView() {
//        rv_saved_city.apply {
//            layoutManager = LinearLayoutManager(this@SavedCityActivity)
//            setHasFixedSize(true)
//            adapter = mAdapter
//        }
//
//        ItemTouchHelper(RecyclerItemTouchHelper(this@SavedCityActivity)).attachToRecyclerView(
//            rv_saved_city
//        )
//
//        mAdapter.setOnItemClickListener {
//            startActivity(
//                Intent(
//                    this@SavedCityActivity,
//                    WeatherDetailsActivity::class.java
//                ).putExtra(WeatherDetailsActivity.CITY, it.id.toString())
//            )
//        }
//    }

    fun onSearchTextClicked(view: View) {
        val intent = Intent(this@SavedCityActivity, SearchActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            Pair.create(txt_city_search, getString(R.string.label_search_hint))
        )
        startActivity(intent, options.toBundle())
        Handler(Looper.myLooper()!!).postDelayed({ finish() }, 1000)
    }

    fun onBackButtonClicked2(view: View) {
        onBackPressed()
        finish()
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is SavedCityAdapter.Holder) {
            val pos = viewHolder.adapterPosition
            val cities = mAdapter.differ.currentList[pos]
            viewModel.updateSavedCities(
                CityRepository(CityDatabase(this@SavedCityActivity)),
                CityUpdate(cities.id, 0)
            )

            Snackbar.make(container, "City removed from saved items", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.updateSavedCities(
                        CityRepository(CityDatabase(this@SavedCityActivity)),
                        CityUpdate(cities.id, 1)
                    )
                }
                setBackgroundTint(resources.getColor(R.color.colorPrimary))
                setActionTextColor(resources.getColor(R.color.color_grey))
                show()
            }

        }

    }
}