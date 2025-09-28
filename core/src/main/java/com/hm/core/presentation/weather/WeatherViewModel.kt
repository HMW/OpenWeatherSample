package com.hm.core.presentation.weather

import com.hm.core.common.Result
import com.hm.core.domain.model.Location
import com.hm.core.domain.usecase.*
import com.hm.core.presentation.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val refreshWeatherUseCase: RefreshWeatherUseCase,
    private val locationUseCase: LocationUseCase,
    private val weatherAnalysisUseCase: WeatherAnalysisUseCase,
    private val weatherValidationUseCase: WeatherValidationUseCase
) : BaseViewModel<WeatherUiState, WeatherEvent>() {

    private var currentLatitude: Double = 25.0330 // 台北預設位置
    private var currentLongitude: Double = 121.5654

    override fun createInitialState(): WeatherUiState {
        return WeatherUiState()
    }

    override fun handleEvent(event: WeatherEvent) {
        when (event) {
            is WeatherEvent.LoadWeather -> loadWeather()
            is WeatherEvent.RefreshWeather -> refreshWeather()
            is WeatherEvent.ClearError -> clearError()
            is WeatherEvent.SearchLocation -> searchLocation(event.query)
            is WeatherEvent.SelectLocation -> selectLocation(event.location)
            is WeatherEvent.UpdateLocation -> updateLocation(event.latitude, event.longitude)
        }
    }

    override fun handleError(error: Exception) {
        setState(uiState.value.copy(
            isLoading = false,
            isRefreshing = false,
            error = error.message ?: "發生未知錯誤"
        ))
    }

    private fun loadWeather() {
        launch {
            setState(uiState.value.copy(isLoading = true, error = null))
            
            // 驗證座標
            val validationResult = weatherValidationUseCase.validateCoordinates(
                currentLatitude, currentLongitude
            )
            
            if (validationResult is Result.Error) {
                setState(uiState.value.copy(
                    isLoading = false,
                    error = "無效的座標位置"
                ))
                return@launch
            }
            
            // 取得天氣資料
            val weatherResult = getCurrentWeatherUseCase.invoke(
                GetCurrentWeatherParams(currentLatitude, currentLongitude)
            )
            
            when (weatherResult) {
                is Result.Success -> {
                    val analysis = weatherAnalysisUseCase.analyzeWeather(weatherResult.data)
                    setState(uiState.value.copy(
                        isLoading = false,
                        weather = weatherResult.data,
                        analysis = analysis,
                        error = null
                    ))
                }
                is Result.Error -> {
                    setState(uiState.value.copy(
                        isLoading = false,
                        error = "無法取得天氣資料：${weatherResult.exception.message}"
                    ))
                }
                is Result.Loading -> {
                    // 保持載入狀態
                }
            }
        }
    }

    private fun refreshWeather() {
        launch {
            setState(uiState.value.copy(isRefreshing = true, error = null))
            
            val weatherResult = refreshWeatherUseCase.invoke(
                RefreshWeatherParams(currentLatitude, currentLongitude)
            )
            
            when (weatherResult) {
                is Result.Success -> {
                    val analysis = weatherAnalysisUseCase.analyzeWeather(weatherResult.data)
                    setState(uiState.value.copy(
                        isRefreshing = false,
                        weather = weatherResult.data,
                        analysis = analysis,
                        error = null
                    ))
                }
                is Result.Error -> {
                    setState(uiState.value.copy(
                        isRefreshing = false,
                        error = "刷新失敗：${weatherResult.exception.message}"
                    ))
                }
                is Result.Loading -> {
                    // 保持刷新狀態
                }
            }
        }
    }

    private fun clearError() {
        setState(uiState.value.copy(error = null))
    }

    private fun searchLocation(query: String) {
        launch {
            // 驗證查詢字串
            val validationResult = weatherValidationUseCase.validateLocationQuery(query)
            
            if (validationResult is Result.Error) {
                setState(uiState.value.copy(
                    error = "無效的搜尋條件：${validationResult.exception.message}"
                ))
                return@launch
            }
            
            setState(uiState.value.copy(isLoading = true, error = null))
            
            val locationResult = locationUseCase.getLocationByQuery(query)
            
            when (locationResult) {
                is Result.Success -> {
                    if (locationResult.data.isNotEmpty()) {
                        val location = locationResult.data.first()
                        updateLocation(location.latitude, location.longitude)
                        setState(uiState.value.copy(
                            isLoading = false,
                            location = location,
                            error = null
                        ))
                    } else {
                        setState(uiState.value.copy(
                            isLoading = false,
                            error = "找不到指定的位置"
                        ))
                    }
                }
                is Result.Error -> {
                    setState(uiState.value.copy(
                        isLoading = false,
                        error = "搜尋位置失敗：${locationResult.exception.message}"
                    ))
                }
                is Result.Loading -> {
                    // 保持載入狀態
                }
            }
        }
    }

    private fun selectLocation(location: Location) {
        updateLocation(location.latitude, location.longitude)
        setState(uiState.value.copy(location = location))
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        currentLatitude = latitude
        currentLongitude = longitude
        loadWeather()
    }
}
