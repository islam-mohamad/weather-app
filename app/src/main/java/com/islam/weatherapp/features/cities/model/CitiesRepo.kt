package com.islam.weatherapp.features.cities.model

import com.islam.weatherapp.domain.CitiesTable
import com.islam.weatherapp.domain.FavoriteCityIdsTable
import com.islam.weatherapp.entities.City
import com.islam.weatherapp.entities.FavoriteCityId
import com.islam.weatherapp.utils.AppConstants
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor
import javax.inject.Inject

class CitiesRepo @Inject constructor(
    private val citiesTable: CitiesTable,
    private val favoriteCityIdsTable: FavoriteCityIdsTable,
    private val api: Api,
    private val executor: Executor
) {

    fun queryCityByName(cityName: String) =
        citiesTable.queryCityByName("%$cityName%")
            .subscribeOn(Schedulers.computation())

    fun queryCitiesByIds(ids: List<Long>) =
        citiesTable.queryCitiesByIds(ids).subscribeOn(Schedulers.io())

    fun queryFavoriteCityIds(): Flowable<List<FavoriteCityId>> =
        favoriteCityIdsTable.queryFavoriteCityIds().subscribeOn(Schedulers.io())

    fun insertFavoriteCityId(favoriteCityId: FavoriteCityId) {
        executor.execute {
            favoriteCityIdsTable.insertFavoriteCityId(favoriteCityId)
        }
    }

    fun deleteFavoriteCityId(favoriteCityId: FavoriteCityId) {
        executor.execute {
            favoriteCityIdsTable.deleteFavoriteCityId(favoriteCityId)
        }
    }

    fun insertCity(city: City) {
        executor.execute {
            citiesTable.insertCity(city)
        }
    }

    fun queryCitiesCount() = citiesTable.queryCitiesCount().subscribeOn(Schedulers.io())

    fun queryFiveCities() =
        citiesTable.queryFiveCities()
            .subscribeOn(Schedulers.io())

    fun getForecastByCoord(lat: Double, lon: Double) =
        api.getForecastByCoord(lat, lon, AppConstants.API_KEY).subscribeOn(Schedulers.io())
}