package com.hm.core.presentation.weather

import com.hm.core.common.Result
import com.hm.core.domain.model.Weather
import com.hm.core.domain.model.CurrentWeatherInfo
import com.hm.core.domain.model.WeatherCondition
import com.hm.core.domain.model.Location
import com.hm.core.domain.usecase.*
import io.mockk.*
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class WeatherViewModelTest {

    private lateinit var getCurrentWeatherUseCase: GetCurrentWeatherUseCase
    private lateinit var getWeatherForecastUseCase: GetWeatherForecastUseCase
    private lateinit var refreshWeatherUseCase: RefreshWeatherUseCase
    private lateinit var locationUseCase: LocationUseCase
    private lateinit var weatherAnalysisUseCase: WeatherAnalysisUseCase
    private lateinit var weatherValidationUseCase: WeatherValidationUseCase
    private lateinit var viewModel: WeatherViewModel

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

    private val testLocation = Location(
        name = "Taipei",
        latitude = 25.0330,
        longitude = 121.5654,
        country = "TW",
        state = "Taipei"
    )

    @Before
    fun setup() {
        getCurrentWeatherUseCase = mockk()
        getWeatherForecastUseCase = mockk()
        refreshWeatherUseCase = mockk()
        locationUseCase = mockk()
        weatherAnalysisUseCase = mockk()
        weatherValidationUseCase = mockk()
        
        viewModel = WeatherViewModel(
            getCurrentWeatherUseCase,
            getWeatherForecastUseCase,
            refreshWeatherUseCase,
            locationUseCase,
            weatherAnalysisUseCase,
            weatherValidationUseCase
        )
    }

    @Test
    fun `initial state should be correct`() {
        // When
        val initialState = viewModel.uiState.value

        // Then
        assertFalse(initialState.isLoading)
        assertNull(initialState.weather)
        assertNull(initialState.location)
        assertNull(initialState.analysis)
        assertNull(initialState.error)
        assertFalse(initialState.isRefreshing)
    }

    @Test
    fun `clearError should clear error state`() {
        // When
        viewModel.handleEvent(WeatherEvent.ClearError)

        // Then
        val state = viewModel.uiState.value
        assertNull(state.error)
    }
}