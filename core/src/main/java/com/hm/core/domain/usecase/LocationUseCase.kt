package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.model.Location
import javax.inject.Inject

data class GetLocationParams(
    val query: String
)

data class GetCurrentLocationParams(
    val latitude: Double,
    val longitude: Double
)

class LocationUseCase @Inject constructor() {
    
    suspend fun getLocationByQuery(query: String): Result<List<Location>> {
        return try {
            // TODO: 實作地理編碼 API 呼叫
            // 這裡先返回模擬資料
            val mockLocation = Location(
                name = query,
                latitude = 25.0330,
                longitude = 121.5654,
                country = "TW",
                state = "Taipei"
            )
            Result.Success(listOf(mockLocation))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    suspend fun getLocationByCoordinates(latitude: Double, longitude: Double): Result<Location> {
        return try {
            // TODO: 實作反向地理編碼 API 呼叫
            // 這裡先返回模擬資料
            val mockLocation = Location(
                name = "Taipei",
                latitude = latitude,
                longitude = longitude,
                country = "TW",
                state = "Taipei"
            )
            Result.Success(mockLocation)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
