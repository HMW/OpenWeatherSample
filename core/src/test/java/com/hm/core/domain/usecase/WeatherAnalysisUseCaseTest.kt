package com.hm.core.domain.usecase

import com.hm.core.domain.model.Weather
import com.hm.core.domain.model.CurrentWeatherInfo
import com.hm.core.domain.model.WeatherCondition
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class WeatherAnalysisUseCaseTest {

    private lateinit var useCase: WeatherAnalysisUseCase

    @Before
    fun setup() {
        useCase = WeatherAnalysisUseCase()
    }

    @Test
    fun `analyzeWeather should categorize freezing temperature correctly`() {
        // Given
        val weather = createTestWeather(temperature = -5.0, condition = "Clear")

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertEquals(TemperatureCategory.FREEZING, analysis.temperatureCategory)
        assertEquals(com.hm.core.domain.usecase.WeatherCondition.CLEAR, analysis.weatherCondition)
    }

    @Test
    fun `analyzeWeather should categorize hot temperature correctly`() {
        // Given
        val weather = createTestWeather(temperature = 35.0, condition = "Clear")

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertEquals(TemperatureCategory.HOT, analysis.temperatureCategory)
        assertEquals(com.hm.core.domain.usecase.WeatherCondition.CLEAR, analysis.weatherCondition)
    }

    @Test
    fun `analyzeWeather should categorize rainy weather correctly`() {
        // Given
        val weather = createTestWeather(temperature = 20.0, condition = "Rain")

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertEquals(TemperatureCategory.MILD, analysis.temperatureCategory)
        assertEquals(com.hm.core.domain.usecase.WeatherCondition.RAINY, analysis.weatherCondition)
    }

    @Test
    fun `analyzeWeather should categorize cloudy weather correctly`() {
        // Given
        val weather = createTestWeather(temperature = 20.0, condition = "Clouds")

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertEquals(TemperatureCategory.MILD, analysis.temperatureCategory)
        assertEquals(com.hm.core.domain.usecase.WeatherCondition.CLOUDY, analysis.weatherCondition)
    }

    @Test
    fun `analyzeWeather should categorize stormy weather correctly`() {
        // Given
        val weather = createTestWeather(temperature = 20.0, condition = "Thunderstorm")

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertEquals(TemperatureCategory.MILD, analysis.temperatureCategory)
        assertEquals(com.hm.core.domain.usecase.WeatherCondition.STORMY, analysis.weatherCondition)
    }

    @Test
    fun `analyzeWeather should generate appropriate recommendation for hot weather`() {
        // Given
        val weather = createTestWeather(temperature = 35.0, condition = "Clear")

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertTrue(analysis.recommendation.contains("炎熱"))
    }

    @Test
    fun `analyzeWeather should generate appropriate recommendation for rainy weather`() {
        // Given
        val weather = createTestWeather(temperature = 20.0, condition = "Rain")

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertTrue(analysis.recommendation.contains("降雨"))
    }

    @Test
    fun `analyzeWeather should generate appropriate recommendation for comfortable weather`() {
        // Given
        val weather = createTestWeather(temperature = 22.0, condition = "Clear", humidity = 50, windSpeed = 2.0)

        // When
        val analysis = useCase.analyzeWeather(weather)

        // Then
        assertTrue(analysis.recommendation.contains("舒適"))
    }

    private fun createTestWeather(
        temperature: Double,
        condition: String,
        humidity: Int = 65,
        windSpeed: Double = 3.5
    ): Weather {
        return Weather(
            latitude = 25.0330,
            longitude = 121.5654,
            timezone = "Asia/Taipei",
            timezoneOffset = 28800,
            current = CurrentWeatherInfo(
                timestamp = 1640995200L,
                sunrise = 1640952000L,
                sunset = 1640995200L,
                temperature = temperature,
                feelsLike = temperature + 2.0,
                pressure = 1013,
                humidity = humidity,
                dewPoint = 15.0,
                uvIndex = 5.0,
                clouds = 0,
                visibility = 10000,
                windSpeed = windSpeed,
                windDirection = 180,
                windGust = 5.0,
                weather = listOf(WeatherCondition(800, condition, "description", "01d"))
            ),
            hourly = emptyList(),
            daily = emptyList(),
            alerts = null,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
