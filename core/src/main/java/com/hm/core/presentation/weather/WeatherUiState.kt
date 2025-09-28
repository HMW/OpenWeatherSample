package com.hm.core.presentation.weather

import com.hm.core.common.Result
import com.hm.core.domain.model.Weather
import com.hm.core.domain.model.Location
import com.hm.core.domain.usecase.WeatherAnalysis

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val location: Location? = null,
    val analysis: WeatherAnalysis? = null,
    val error: String? = null,
    val isRefreshing: Boolean = false
)

sealed class WeatherEvent {
    object LoadWeather : WeatherEvent()
    object RefreshWeather : WeatherEvent()
    object ClearError : WeatherEvent()
    data class SearchLocation(val query: String) : WeatherEvent()
    data class SelectLocation(val location: Location) : WeatherEvent()
    data class UpdateLocation(val latitude: Double, val longitude: Double) : WeatherEvent()
}
