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
            is WeatherEvent.UpdateSearchQuery -> updateSearchQuery(event.query)
            is WeatherEvent.ClearSearchResults -> clearSearchResults()
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
            // 記錄載入天氣時使用的座標
            android.util.Log.d("WeatherViewModel", "loadWeather called with coordinates: lat=${currentLatitude}, lon=${currentLongitude}")
            
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
                        // 保持現有的 location 狀態
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
            // 記錄刷新天氣時使用的座標
            android.util.Log.d("WeatherViewModel", "refreshWeather called with coordinates: lat=${currentLatitude}, lon=${currentLongitude}")
            
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
                        // 保持現有的 location 狀態
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
                    isSearching = false,
                    searchResults = emptyList(),
                    error = "無效的搜尋條件：${validationResult.exception.message}"
                ))
                return@launch
            }
            
            setState(uiState.value.copy(isSearching = true, error = null))
            
            val locationResult = locationUseCase.getLocationByQuery(query)
            
            when (locationResult) {
                is Result.Success -> {
                    setState(uiState.value.copy(
                        isSearching = false,
                        searchResults = locationResult.data,
                        error = null
                    ))
                }
                is Result.Error -> {
                    setState(uiState.value.copy(
                        isSearching = false,
                        searchResults = emptyList(),
                        error = "搜尋位置失敗：${locationResult.exception.message}"
                    ))
                }
                is Result.Loading -> {
                    // 保持搜尋狀態
                }
            }
        }
    }

    private fun selectLocation(location: Location) {
        // 記錄選擇的位置資訊
        android.util.Log.d("WeatherViewModel", "selectLocation called with location: name='${location.name}', lat=${location.latitude}, lon=${location.longitude}, country='${location.country}', state='${location.state}'")
        
        currentLatitude = location.latitude
        currentLongitude = location.longitude
        setState(uiState.value.copy(location = location))
        refreshWeather()
    }

    private fun updateLocation(latitude: Double, longitude: Double) {
        currentLatitude = latitude
        currentLongitude = longitude
        refreshWeather() // 使用 refreshWeather 強制從遠端獲取新資料
    }
    
    private fun updateSearchQuery(query: String) {
        setState(uiState.value.copy(searchQuery = query))
    }
    
    private fun clearSearchResults() {
        setState(uiState.value.copy(
            searchQuery = "",
            searchResults = emptyList(),
            isSearching = false
        ))
    }
}
