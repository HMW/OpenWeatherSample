package com.hm.core.database.dao

import androidx.room.*
import com.hm.core.database.entity.WeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather WHERE latitude = :lat AND longitude = :lon")
    fun getWeatherByLocation(lat: Double, lon: Double): Flow<WeatherEntity?>

    @Query("SELECT * FROM weather WHERE id = :id")
    suspend fun getWeatherById(id: String): WeatherEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeather(weather: WeatherEntity)

    @Update
    suspend fun updateWeather(weather: WeatherEntity)

    @Delete
    suspend fun deleteWeather(weather: WeatherEntity)

    @Query("DELETE FROM weather WHERE lastUpdated < :timestamp")
    suspend fun deleteOldWeather(timestamp: Long)
}

