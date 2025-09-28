package com.hm.core.data.repository

import com.hm.core.common.Constants
import com.hm.core.common.ErrorHandler
import com.hm.core.common.Result
import com.hm.core.common.WeatherException
import com.hm.core.data.datasource.local.WeatherLocalDataSource
import com.hm.core.data.datasource.remote.WeatherRemoteDataSource
import com.hm.core.domain.model.Weather
import com.hm.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val localDataSource: WeatherLocalDataSource
) : WeatherRepository {

    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<Weather> {
        // 先嘗試從本地快取取得
        val cachedResult = localDataSource.getWeather(latitude, longitude)
        
        return when (cachedResult) {
            is Result.Success -> {
                val cachedWeather = cachedResult.data
                if (cachedWeather != null && isCacheValid(cachedWeather.lastUpdated)) {
                    // 快取有效，直接返回
                    Result.Success(cachedWeather)
                } else {
                    // 快取無效或不存在，從遠端取得
                    fetchFromRemote(latitude, longitude)
                }
            }
            is Result.Error -> {
                // 本地取得失敗，從遠端取得
                fetchFromRemote(latitude, longitude)
            }
            is Result.Loading -> {
                // 不應該發生，但處理一下
                fetchFromRemote(latitude, longitude)
            }
        }
    }

    override fun getWeatherFlow(latitude: Double, longitude: Double): Flow<Result<Weather>> {
        return flow {
            emit(Result.Loading)
            
            // 先發射本地快取資料
            localDataSource.getWeatherFlow(latitude, longitude).collect { localResult ->
                when (localResult) {
                    is Result.Success -> {
                        val cachedWeather = localResult.data
                        if (cachedWeather != null && isCacheValid(cachedWeather.lastUpdated)) {
                            emit(Result.Success(cachedWeather))
                        } else {
                            // 快取無效，從遠端取得
                            val remoteResult = fetchFromRemote(latitude, longitude)
                            emit(remoteResult)
                        }
                    }
                    is Result.Error -> {
                        // 本地取得失敗，從遠端取得
                        val remoteResult = fetchFromRemote(latitude, longitude)
                        emit(remoteResult)
                    }
                    is Result.Loading -> {
                        // 繼續等待
                    }
                }
            }
        }
    }

    override suspend fun refreshWeather(latitude: Double, longitude: Double): Result<Weather> {
        return fetchFromRemote(latitude, longitude)
    }

    override suspend fun clearCache(): Result<Unit> {
        return try {
            localDataSource.clearCache()
            Result.Success(Unit)
        } catch (e: Exception) {
            val weatherException = ErrorHandler.handleException(e)
            Result.Error(weatherException)
        }
    }

    private suspend fun fetchFromRemote(latitude: Double, longitude: Double): Result<Weather> {
        return try {
            // 從遠端取得資料
            val remoteResult = remoteDataSource.getWeatherData(
                latitude = latitude,
                longitude = longitude,
                apiKey = Constants.API_KEY // 需要設定 API Key
            )
            
            when (remoteResult) {
                is Result.Success -> {
                    // 儲存到本地快取
                    localDataSource.saveWeather(remoteResult.data)
                    remoteResult
                }
                is Result.Error -> {
                    val weatherException = ErrorHandler.handleException(remoteResult.exception)
                    Result.Error(weatherException)
                }
                is Result.Loading -> Result.Error(WeatherException.NetworkException("Unexpected loading state"))
            }
        } catch (e: Exception) {
            val weatherException = ErrorHandler.handleException(e)
            Result.Error(weatherException)
        }
    }

    private fun isCacheValid(lastUpdated: Long): Boolean {
        val currentTime = System.currentTimeMillis()
        val cacheAge = currentTime - lastUpdated
        val cacheValidDuration = Constants.CACHE_DURATION_HOURS * 60 * 60 * 1000L
        return cacheAge < cacheValidDuration
    }
}
