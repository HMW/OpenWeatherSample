package com.hm.core.network

import com.hm.core.network.dto.CurrentWeatherResponse
import com.hm.core.network.dto.ForecastResponse
import com.hm.core.network.dto.DailyForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "zh_tw"
    ): CurrentWeatherResponse
    
    @GET("forecast")
    suspend fun getForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "zh_tw"
    ): ForecastResponse
    
    @GET("forecast/daily")
    suspend fun getDailyForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String,
        @Query("cnt") count: Int = 7,
        @Query("units") units: String = "metric",
        @Query("lang") language: String = "zh_tw"
    ): DailyForecastResponse
}

