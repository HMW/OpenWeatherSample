package com.hm.core.data.datasource.local

import com.hm.core.common.Result
import com.hm.core.database.dao.WeatherDao
import com.hm.core.database.entity.WeatherEntity
import com.hm.core.domain.model.Weather
import com.hm.core.data.mapper.WeatherMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherLocalDataSource @Inject constructor(
    private val weatherDao: WeatherDao,
    private val mapper: WeatherMapper
) {
    fun getWeatherFlow(latitude: Double, longitude: Double): Flow<Result<Weather?>> {
        return weatherDao.getWeatherByLocation(latitude, longitude).map { entity ->
            if (entity != null) {
                Result.Success(mapper.mapToDomain(entity))
            } else {
                Result.Success(null)
            }
        }
    }

    suspend fun getWeather(latitude: Double, longitude: Double): Result<Weather?> {
        return try {
            val entity = weatherDao.getWeatherById("${latitude}_${longitude}")
            if (entity != null) {
                Result.Success(mapper.mapToDomain(entity))
            } else {
                Result.Success(null)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun saveWeather(weather: Weather): Result<Unit> {
        return try {
            val entity = mapper.mapToEntity(weather)
            weatherDao.insertWeather(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun clearCache(): Result<Unit> {
        return try {
            val currentTime = System.currentTimeMillis()
            weatherDao.deleteOldWeather(currentTime)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}



