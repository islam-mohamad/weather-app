package com.islam.weatherapp.features.cities.view.callback

import com.islam.weatherapp.binding.RecyclerViewCallback
import com.islam.weatherapp.entities.City

interface CityCallback : RecyclerViewCallback {

    fun onCityClicked(city: City)
    fun onAddCityClicked(city: City)
    fun onDeleteCityClicked(city: City)
}