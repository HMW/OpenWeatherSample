package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.common.WeatherException
import javax.inject.Inject

data class ValidateCoordinatesParams(
    val latitude: Double,
    val longitude: Double
)

class WeatherValidationUseCase @Inject constructor() {
    
    fun validateCoordinates(latitude: Double, longitude: Double): Result<Unit> {
        return when {
            latitude < -90 || latitude > 90 -> {
                Result.Error(WeatherException.LocationException("緯度必須在 -90 到 90 度之間"))
            }
            longitude < -180 || longitude > 180 -> {
                Result.Error(WeatherException.LocationException("經度必須在 -180 到 180 度之間"))
            }
            else -> Result.Success(Unit)
        }
    }
    
    fun validateLocationQuery(query: String): Result<Unit> {
        return when {
            query.isBlank() -> {
                Result.Error(WeatherException.LocationException("位置查詢不能為空"))
            }
            query.length < 2 -> {
                Result.Error(WeatherException.LocationException("位置查詢至少需要 2 個字符"))
            }
            query.length > 100 -> {
                Result.Error(WeatherException.LocationException("位置查詢不能超過 100 個字符"))
            }
            else -> Result.Success(Unit)
        }
    }
}
