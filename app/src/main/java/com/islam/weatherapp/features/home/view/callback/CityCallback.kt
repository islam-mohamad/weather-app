package com.islam.weatherapp.features.home.view.callback

import com.islam.weatherapp.binding.RecyclerViewCallback
import com.islam.weatherapp.features.home.model.City

interface CityCallback : RecyclerViewCallback {

    fun onCityClicked(city: City)
}