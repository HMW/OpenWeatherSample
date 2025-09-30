package com.hm.core.data.mapper

import com.google.gson.Gson
import com.hm.core.database.entity.WeatherEntity
import com.hm.core.domain.model.*
import com.hm.core.network.dto.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherMapper @Inject constructor(private val gson: Gson) {

    fun mapToDomain(currentWeather: CurrentWeatherResponse, forecast: ForecastResponse, dailyForecast: DailyForecastResponse): Weather {
        return Weather(
            latitude = currentWeather.coord.latitude,
            longitude = currentWeather.coord.longitude,
            timezone = "UTC", // Current API doesn't provide timezone name
            timezoneOffset = currentWeather.timezone,
            current = mapCurrentWeather(currentWeather),
            hourly = mapForecastToHourly(forecast.list),
            daily = if (dailyForecast.list.isNotEmpty()) {
                mapDailyForecastToDaily(dailyForecast.list)
            } else {
                // 如果 16 天預報 API 失敗，使用 5 天預報
                mapForecastToDaily(forecast.list)
            },
            alerts = null, // Current API doesn't provide alerts
            lastUpdated = System.currentTimeMillis()
        )
    }

    fun mapToEntity(weather: Weather): WeatherEntity {
        return WeatherEntity(
            id = "${weather.latitude}_${weather.longitude}",
            latitude = weather.latitude,
            longitude = weather.longitude,
            timezone = weather.timezone,
            currentWeather = gson.toJson(weather.current),
            hourlyForecast = gson.toJson(weather.hourly),
            dailyForecast = gson.toJson(weather.daily),
            lastUpdated = weather.lastUpdated
        )
    }

    fun mapToDomain(entity: WeatherEntity): Weather {
        return Weather(
            latitude = entity.latitude,
            longitude = entity.longitude,
            timezone = entity.timezone,
            timezoneOffset = 0, // 從 entity 中沒有儲存，使用預設值
            current = gson.fromJson(entity.currentWeather, CurrentWeatherInfo::class.java),
            hourly = gson.fromJson(entity.hourlyForecast, Array<HourlyWeatherInfo>::class.java).toList(),
            daily = gson.fromJson(entity.dailyForecast, Array<DailyWeatherInfo>::class.java).toList(),
            alerts = null, // 從 entity 中沒有儲存警報
            lastUpdated = entity.lastUpdated
        )
    }

    private fun mapCurrentWeather(current: CurrentWeatherResponse): CurrentWeatherInfo {
        return CurrentWeatherInfo(
            timestamp = current.dt,
            sunrise = current.sys.sunrise,
            sunset = current.sys.sunset,
            temperature = current.main.temp,
            feelsLike = current.main.feelsLike,
            pressure = current.main.pressure,
            humidity = current.main.humidity,
            dewPoint = 0.0, // Current API doesn't provide dew point
            uvIndex = 0.0, // Current API doesn't provide UV index
            clouds = current.clouds.all,
            visibility = current.visibility,
            windSpeed = current.wind.speed,
            windDirection = current.wind.deg,
            windGust = current.wind.gust ?: 0.0,
            weather = current.weather.map { mapWeatherCondition(it) }
        )
    }

    private fun mapForecastToHourly(forecastList: List<ForecastItemDto>): List<HourlyWeatherInfo> {
        return forecastList.take(24).map { item ->
            HourlyWeatherInfo(
                timestamp = item.dt,
                temperature = item.main.temp,
                feelsLike = item.main.feelsLike,
                pressure = item.main.pressure,
                humidity = item.main.humidity,
                dewPoint = 0.0, // Forecast API doesn't provide dew point
                uvIndex = 0.0, // Forecast API doesn't provide UV index
                clouds = item.clouds.all,
                visibility = item.visibility,
                windSpeed = item.wind.speed,
                windDirection = item.wind.deg,
                windGust = item.wind.gust ?: 0.0,
                weather = item.weather.map { mapWeatherCondition(it) },
                precipitationProbability = item.pop
            )
        }
    }

    private fun mapForecastToDaily(forecastList: List<ForecastItemDto>): List<DailyWeatherInfo> {
        // Group forecast items by day and create daily summaries
        val dailyGroups = forecastList.groupBy { item ->
            java.util.Calendar.getInstance().apply {
                timeInMillis = item.dt * 1000
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis / 1000
        }

        return dailyGroups.values.take(5).map { dayItems ->
            val firstItem = dayItems.first()
            val temps = dayItems.map { it.main.temp }
            val feelsLike = dayItems.map { it.main.feelsLike }
            
            DailyWeatherInfo(
                timestamp = firstItem.dt,
                sunrise = 0L, // Forecast API doesn't provide sunrise/sunset
                sunset = 0L,
                temperature = TemperatureRange(
                    day = temps.maxOrNull() ?: 0.0,
                    min = temps.minOrNull() ?: 0.0,
                    max = temps.maxOrNull() ?: 0.0,
                    night = temps.minOrNull() ?: 0.0,
                    evening = temps.average(),
                    morning = temps.average()
                ),
                feelsLike = FeelsLikeRange(
                    day = feelsLike.maxOrNull() ?: 0.0,
                    night = feelsLike.minOrNull() ?: 0.0,
                    evening = feelsLike.average(),
                    morning = feelsLike.average()
                ),
                pressure = firstItem.main.pressure,
                humidity = firstItem.main.humidity,
                dewPoint = 0.0,
                windSpeed = firstItem.wind.speed,
                windDirection = firstItem.wind.deg,
                weather = firstItem.weather.map { mapWeatherCondition(it) },
                clouds = firstItem.clouds.all,
                precipitationProbability = dayItems.map { it.pop }.average(),
                uvIndex = 0.0
            )
        }
    }

    private fun mapDailyForecastToDaily(dailyForecastList: List<DailyForecastItemDto>): List<DailyWeatherInfo> {
        return dailyForecastList.take(7).map { daily ->
            DailyWeatherInfo(
                timestamp = daily.dt,
                sunrise = daily.sunrise,
                sunset = daily.sunset,
                temperature = TemperatureRange(
                    day = daily.temp.day,
                    min = daily.temp.min,
                    max = daily.temp.max,
                    night = daily.temp.night,
                    evening = daily.temp.eve,
                    morning = daily.temp.morn
                ),
                feelsLike = FeelsLikeRange(
                    day = daily.feelsLike.day,
                    night = daily.feelsLike.night,
                    evening = daily.feelsLike.eve,
                    morning = daily.feelsLike.morn
                ),
                pressure = daily.pressure,
                humidity = daily.humidity,
                dewPoint = 0.0, // Daily forecast API doesn't provide dew point
                windSpeed = daily.speed,
                windDirection = daily.deg,
                weather = daily.weather.map { mapWeatherCondition(it) },
                clouds = daily.clouds,
                precipitationProbability = daily.pop,
                uvIndex = 0.0 // Daily forecast API doesn't provide UV index
            )
        }
    }

    private fun mapWeatherCondition(condition: WeatherConditionDto): WeatherCondition {
        return WeatherCondition(
            id = condition.id,
            main = condition.main,
            description = condition.description,
            icon = condition.icon
        )
    }
}