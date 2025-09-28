package com.hm.core.domain.repository

import com.hm.core.common.Result
import com.hm.core.domain.model.Weather
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather>
    fun getWeatherFlow(latitude: Double, longitude: Double): Flow<Result<Weather>>
    suspend fun refreshWeather(latitude: Double, longitude: Double): Result<Weather>
    suspend fun clearCache(): Result<Unit>
}
