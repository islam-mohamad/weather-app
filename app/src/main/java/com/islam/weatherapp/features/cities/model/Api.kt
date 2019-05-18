package com.islam.weatherapp.features.cities.model

import com.islam.weatherapp.entities.ForecastsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("forecast")
    fun getForecastByCoord(@Query("lat") lat: Double, @Query("lon") lon: Double,
                           @Query("APPID") apiKey:String): Single<ForecastsResponse>
}