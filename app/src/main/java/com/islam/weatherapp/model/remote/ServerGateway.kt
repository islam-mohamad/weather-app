package com.islam.weatherapp.model.remote

import com.islam.weatherapp.entities.ForecastsResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerGateway {
    @GET("forecast")
    fun getForecastByCoord(@Query("lat") lat: Double, @Query("lon") lon: Double,
                           @Query("APPID") apiKey:String): Single<ForecastsResponse>
}