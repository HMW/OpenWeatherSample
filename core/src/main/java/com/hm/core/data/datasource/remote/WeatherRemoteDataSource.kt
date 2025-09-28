package com.hm.core.data.datasource.remote

import com.hm.core.common.Constants
import com.hm.core.common.Result
import com.hm.core.data.mapper.WeatherMapper
import com.hm.core.domain.model.Weather
import com.hm.core.network.OpenWeatherApi
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteDataSource @Inject constructor(
    private val api: OpenWeatherApi,
    private val mapper: WeatherMapper
) {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        apiKey: String,
        units: String = Constants.DEFAULT_UNITS,
        language: String = Constants.DEFAULT_LANGUAGE
    ): Result<Weather> {
        return try {
            val currentWeather = api.getCurrentWeather(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = units,
                language = language
            )
            
            val forecast = api.getForecast(
                latitude = latitude,
                longitude = longitude,
                apiKey = apiKey,
                units = units,
                language = language
            )
            
            Result.Success(mapper.mapToDomain(currentWeather, forecast))
        } catch (e: Exception) {
            Result.Error(com.hm.core.common.ErrorHandler.handleException(e))
        }
    }
}