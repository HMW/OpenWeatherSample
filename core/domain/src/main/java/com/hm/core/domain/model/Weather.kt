package com.hm.core.domain.model

data class Weather(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val timezoneOffset: Int,
    val current: CurrentWeatherInfo,
    val hourly: List<HourlyWeatherInfo>,
    val daily: List<DailyWeatherInfo>,
    val alerts: List<WeatherAlert>?,
    val lastUpdated: Long
)

data class CurrentWeatherInfo(
    val timestamp: Long,
    val sunrise: Long,
    val sunset: Long,
    val temperature: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val uvIndex: Double,
    val clouds: Int,
    val visibility: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val windGust: Double?,
    val weather: List<WeatherCondition>
)

data class HourlyWeatherInfo(
    val timestamp: Long,
    val temperature: Double,
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val uvIndex: Double,
    val clouds: Int,
    val visibility: Int,
    val windSpeed: Double,
    val windDirection: Int,
    val windGust: Double?,
    val weather: List<WeatherCondition>,
    val precipitationProbability: Double
)

data class DailyWeatherInfo(
    val timestamp: Long,
    val sunrise: Long,
    val sunset: Long,
    val temperature: TemperatureRange,
    val feelsLike: FeelsLikeRange,
    val pressure: Int,
    val humidity: Int,
    val dewPoint: Double,
    val windSpeed: Double,
    val windDirection: Int,
    val weather: List<WeatherCondition>,
    val clouds: Int,
    val precipitationProbability: Double,
    val uvIndex: Double
)

data class TemperatureRange(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val evening: Double,
    val morning: Double
)

data class FeelsLikeRange(
    val day: Double,
    val night: Double,
    val evening: Double,
    val morning: Double
)

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class WeatherAlert(
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)
