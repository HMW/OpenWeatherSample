package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.model.Weather
import com.hm.core.domain.model.CurrentWeatherInfo
import com.hm.core.domain.model.WeatherCondition
import com.hm.core.domain.repository.WeatherRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class GetCurrentWeatherUseCaseTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var useCase: GetCurrentWeatherUseCase

    private val testWeather = Weather(
        latitude = 25.0330,
        longitude = 121.5654,
        timezone = "Asia/Taipei",
        timezoneOffset = 28800,
        current = CurrentWeatherInfo(
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
            weather = listOf(WeatherCondition(800, "Clear", "clear sky", "01d"))
        ),
        hourly = emptyList(),
        daily = emptyList(),
        alerts = null,
        lastUpdated = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        weatherRepository = mockk()
        useCase = GetCurrentWeatherUseCase(weatherRepository)
    }

    @Test
    fun `invoke should return weather data when repository succeeds`() = runTest {
        // Given
        val params = GetCurrentWeatherParams(25.0330, 121.5654)
        coEvery { weatherRepository.getCurrentWeather(25.0330, 121.5654) } returns Result.Success(testWeather)

        // When
        val result = useCase.invoke(params)

        // Then
        assertTrue(result is Result.Success)
        assertEquals(testWeather, (result as Result.Success).data)
        coVerify { weatherRepository.getCurrentWeather(25.0330, 121.5654) }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val params = GetCurrentWeatherParams(25.0330, 121.5654)
        val exception = Exception("Network error")
        coEvery { weatherRepository.getCurrentWeather(25.0330, 121.5654) } returns Result.Error(exception)

        // When
        val result = useCase.invoke(params)

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
        coVerify { weatherRepository.getCurrentWeather(25.0330, 121.5654) }
    }
}
