package com.islam.weatherapp.features.cities.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.islam.weatherapp.features.cities.model.CitiesRepo
import javax.inject.Inject

class CitiesViewModelFactory @Inject constructor(private val  citiesRepo: CitiesRepo) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CitiesViewModel(citiesRepo) as T
    }
}