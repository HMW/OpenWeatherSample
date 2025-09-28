package com.hm.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather")
data class WeatherEntity(
    @PrimaryKey
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val currentWeather: String, // JSON string
    val hourlyForecast: String, // JSON string
    val dailyForecast: String, // JSON string
    val lastUpdated: Long
)

