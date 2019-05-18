package com.islam.weatherapp.features.cities.viewmodel

import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.islam.weatherapp.base.BaseViewModel
import com.islam.weatherapp.entities.City
import com.islam.weatherapp.entities.FavoriteCityId
import com.islam.weatherapp.features.cities.model.CitiesRepo
import io.reactivex.android.schedulers.AndroidSchedulers

class CitiesViewModel(private val citiesRepo: CitiesRepo) : BaseViewModel() {

    var favoriteCities: MutableLiveData<List<City>> = MutableLiveData()
    var citiesSearchResult: MutableLiveData<List<City>> = MutableLiveData()
    var citiesCount: MutableLiveData<Int> = MutableLiveData()

    fun insertFavoriteCityId(favoriteCityId: FavoriteCityId) {
        citiesRepo.insertFavoriteCityId(favoriteCityId)
    }


    fun deleteFavoriteCityId(city: City) {
        citiesRepo.deleteFavoriteCityId(FavoriteCityId(city.id))
    }

    fun insertCity(city: City) {
        citiesRepo.insertCity(city)
    }

    fun queryCityByName(cityName: String) {
        if (TextUtils.isEmpty(cityName)) {
            clearSearchResults()
            return
        }
        setIsLoading(true)
//        clearSearchResults()
        compositeDisposable.add(
            citiesRepo.queryCityByName(cityName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    setIsLoading(false)
                    citiesSearchResult.value = result

                }, {
                    setIsLoading(false)
                })
        )
    }

    fun initSearchBox() {
        setIsLoading(true)
        compositeDisposable.add(
            citiesRepo.queryFiveCities()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    setIsLoading(false)
                    citiesSearchResult.value = result

                }, {
                    setIsLoading(false)
                })
        )
    }

    private fun clearSearchResults() {
        setIsLoading(false)
        citiesSearchResult.value = ArrayList()
    }

    fun queryCitiesCount() {
        setIsLoading(true)
        compositeDisposable.add(
            citiesRepo.queryCitiesCount()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ count ->
                   citiesCount.value = count
                }, {

                })
        )
    }


    fun queryFavoriteCities() {
        setIsLoading(true)
        compositeDisposable.add(
            citiesRepo.queryFavoriteCityIds()
                .map {
                    val idsInt: ArrayList<Long> = ArrayList()
                    for (i in 0 until (it.size)) {
                        idsInt.add(it[i].id)
                    }
                    return@map idsInt
                }
                .flatMap {
                    citiesRepo.queryCitiesByIds(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setIsLoading(false)
                    favoriteCities.value = it
                }, {
                    setIsLoading(false)
                }, {
                    setIsLoading(false)
                })
        )
    }

    fun getForecastByCoord(lat: Double, lon: Double) {
        compositeDisposable.add(citiesRepo.getForecastByCoord(lat, lon)
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                it.city
            }
            .subscribe({
                insertFavoriteCityId(FavoriteCityId(it!!.id))
            }, {
                Log.e("okhttp", it.message + "")
            })
        )
    }

}