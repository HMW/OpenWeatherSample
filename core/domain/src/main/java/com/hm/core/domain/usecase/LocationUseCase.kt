package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.model.Location
import com.hm.core.network.GeocodingApi
import com.hm.core.common.Constants
import javax.inject.Inject

data class GetLocationParams(
    val query: String
)

data class GetCurrentLocationParams(
    val latitude: Double,
    val longitude: Double
)

class LocationUseCase @Inject constructor(
    private val geocodingApi: GeocodingApi
) {
    
    suspend fun getLocationByQuery(query: String): Result<List<Location>> {
        return try {
            // 先搜尋本地熱門城市
            val popularCities = getPopularCities()
            val localSearchResults = popularCities.filter { city ->
                city.name.contains(query, ignoreCase = true) ||
                city.state?.contains(query, ignoreCase = true) == true ||
                city.country.contains(query, ignoreCase = true)
            }
            
            if (localSearchResults.isNotEmpty()) {
                // 如果本地搜尋有結果，返回本地結果
                Result.Success(localSearchResults)
            } else {
                // 如果本地沒有結果，使用 OpenWeather Geocoding API
                try {
                    val geocodingResults = geocodingApi.getGeocoding(
                        cityName = query,
                        apiKey = Constants.API_KEY,
                        limit = 5
                    )
                    
                    if (geocodingResults.isNotEmpty()) {
                        val locations = geocodingResults.map { geocoding ->
                            Location(
                                name = geocoding.name,
                                latitude = geocoding.latitude,
                                longitude = geocoding.longitude,
                                country = geocoding.country,
                                state = geocoding.state
                            )
                        }
                        Result.Success(locations)
                    } else {
                        // 如果 API 也沒有結果，返回錯誤
                        Result.Error(Exception("找不到相關城市"))
                    }
                } catch (apiException: Exception) {
                    // 如果 API 呼叫失敗，返回錯誤
                    Result.Error(Exception("搜尋城市時發生錯誤：${apiException.message}"))
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
    
    private fun getPopularCities(): List<Location> {
        return listOf(
            Location(
                name = "台北",
                latitude = 25.0330,
                longitude = 121.5654,
                country = "TW",
                state = "台北市"
            ),
            Location(
                name = "東京",
                latitude = 35.6762,
                longitude = 139.6503,
                country = "JP",
                state = "東京都"
            ),
            Location(
                name = "紐約",
                latitude = 40.7128,
                longitude = -74.0060,
                country = "US",
                state = "紐約州"
            ),
            Location(
                name = "倫敦",
                latitude = 51.5074,
                longitude = -0.1278,
                country = "GB",
                state = "英格蘭"
            ),
            Location(
                name = "巴黎",
                latitude = 48.8566,
                longitude = 2.3522,
                country = "FR",
                state = "法蘭西島"
            ),
            Location(
                name = "雪梨",
                latitude = -33.8688,
                longitude = 151.2093,
                country = "AU",
                state = "新南威爾斯州"
            ),
            Location(
                name = "新加坡",
                latitude = 1.3521,
                longitude = 103.8198,
                country = "SG",
                state = null
            ),
            Location(
                name = "香港",
                latitude = 22.3193,
                longitude = 114.1694,
                country = "HK",
                state = null
            )
        )
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
