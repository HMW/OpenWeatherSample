package com.hm.core.network

import com.hm.core.domain.model.*
import org.junit.Test
import org.junit.Assert.*

class WeatherResponseMapperTest {

    @Test
    fun `test weather condition mapping`() {
        val weatherCondition = WeatherCondition(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )

        assertEquals(800, weatherCondition.id)
        assertEquals("Clear", weatherCondition.main)
        assertEquals("clear sky", weatherCondition.description)
        assertEquals("01d", weatherCondition.icon)
    }

    @Test
    fun `test temperature range mapping`() {
        val temperatureRange = TemperatureRange(
            day = 25.0,
            min = 15.0,
            max = 30.0,
            night = 18.0,
            evening = 22.0,
            morning = 16.0
        )

        assertEquals(25.0, temperatureRange.day, 0.001)
        assertEquals(15.0, temperatureRange.min, 0.001)
        assertEquals(30.0, temperatureRange.max, 0.001)
        assertEquals(18.0, temperatureRange.night, 0.001)
        assertEquals(22.0, temperatureRange.evening, 0.001)
        assertEquals(16.0, temperatureRange.morning, 0.001)
    }

    @Test
    fun `test feels like range mapping`() {
        val feelsLikeRange = FeelsLikeRange(
            day = 27.0,
            night = 20.0,
            evening = 24.0,
            morning = 18.0
        )

        assertEquals(27.0, feelsLikeRange.day, 0.001)
        assertEquals(20.0, feelsLikeRange.night, 0.001)
        assertEquals(24.0, feelsLikeRange.evening, 0.001)
        assertEquals(18.0, feelsLikeRange.morning, 0.001)
    }

    @Test
    fun `test weather alert mapping`() {
        val weatherAlert = WeatherAlert(
            senderName = "NWS",
            event = "Heat Advisory",
            start = 1640995200L,
            end = 1641000000L,
            description = "Heat advisory in effect"
        )

        assertEquals("NWS", weatherAlert.senderName)
        assertEquals("Heat Advisory", weatherAlert.event)
        assertEquals(1640995200L, weatherAlert.start)
        assertEquals(1641000000L, weatherAlert.end)
        assertEquals("Heat advisory in effect", weatherAlert.description)
    }

    @Test
    fun `test complete weather response structure`() {
        val weatherCondition = WeatherCondition(
            id = 800,
            main = "Clear",
            description = "clear sky",
            icon = "01d"
        )

        val currentWeather = CurrentWeatherInfo(
            timestamp = 1640995200L,
            sunrise = 1640952000L,
            sunset = 1640995200L,
            temperature = 20.0,
            feelsLike = 22.0,
            pressure = 1013,
            humidity = 65,
            dewPoint = 15.0,
            uvIndex = 5.0,
            clouds = 0,
            visibility = 10000,
            windSpeed = 3.5,
            windDirection = 180,
            windGust = 5.0,
            weather = listOf(weatherCondition)
        )

        val hourlyWeather = HourlyWeatherInfo(
            timestamp = 1640995200L,
            temperature = 20.0,
            feelsLike = 22.0,
            pressure = 1013,
            humidity = 65,
            dewPoint = 15.0,
            uvIndex = 5.0,
            clouds = 0,
            visibility = 10000,
            windSpeed = 3.5,
            windDirection = 180,
            windGust = 5.0,
            weather = listOf(weatherCondition),
            precipitationProbability = 0.0
        )

        val temperatureRange = TemperatureRange(
            day = 25.0,
            min = 15.0,
            max = 30.0,
            night = 18.0,
            evening = 22.0,
            morning = 16.0
        )

        val feelsLikeRange = FeelsLikeRange(
            day = 27.0,
            night = 20.0,
            evening = 24.0,
            morning = 18.0
        )

        val dailyWeather = DailyWeatherInfo(
            timestamp = 1640995200L,
            sunrise = 1640952000L,
            sunset = 1640995200L,
            temperature = temperatureRange,
            feelsLike = feelsLikeRange,
            pressure = 1013,
            humidity = 65,
            dewPoint = 15.0,
            windSpeed = 3.5,
            windDirection = 180,
            weather = listOf(weatherCondition),
            clouds = 0,
            precipitationProbability = 0.0,
            uvIndex = 5.0
        )

        val weatherResponse = Weather(
            latitude = 25.0330,
            longitude = 121.5654,
            timezone = "Asia/Taipei",
            timezoneOffset = 28800,
            current = currentWeather,
            hourly = listOf(hourlyWeather),
            daily = listOf(dailyWeather),
            alerts = null,
            lastUpdated = 1640995200L
        )

        // 驗證主要結構
        assertEquals(25.0330, weatherResponse.latitude, 0.001)
        assertEquals(121.5654, weatherResponse.longitude, 0.001)
        assertEquals("Asia/Taipei", weatherResponse.timezone)
        assertEquals(28800, weatherResponse.timezoneOffset)

        // 驗證當前天氣
        assertEquals(20.0, weatherResponse.current.temperature, 0.001)
        assertEquals(22.0, weatherResponse.current.feelsLike, 0.001)
        assertEquals(1013, weatherResponse.current.pressure)
        assertEquals(65, weatherResponse.current.humidity)

        // 驗證小時預報
        assertEquals(1, weatherResponse.hourly.size)
        assertEquals(20.0, weatherResponse.hourly.first().temperature, 0.001)

        // 驗證每日預報
        assertEquals(1, weatherResponse.daily.size)
        assertEquals(25.0, weatherResponse.daily.first().temperature.day, 0.001)
        assertEquals(15.0, weatherResponse.daily.first().temperature.min, 0.001)
        assertEquals(30.0, weatherResponse.daily.first().temperature.max, 0.001)
    }
}