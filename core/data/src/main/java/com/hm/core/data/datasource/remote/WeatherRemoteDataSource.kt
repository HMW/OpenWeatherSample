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
            
            // 創建空的 16 天預報回應，因為免費 API 不支援
            val dailyForecast = com.hm.core.network.dto.DailyForecastResponse(
                city = com.hm.core.network.dto.DailyForecastCityDto(
                    id = 0,
                    name = "",
                    coord = com.hm.core.network.dto.Coordinates(0.0, 0.0),
                    country = "",
                    population = 0,
                    timezone = 0
                ),
                cod = "200",
                message = 0.0,
                cnt = 0,
                list = emptyList()
            )
            
            Result.Success(mapper.mapToDomain(currentWeather, forecast, dailyForecast))
        } catch (e: Exception) {
            Result.Error(com.hm.core.network.ErrorHandler.handleException(e))
        }
    }
}