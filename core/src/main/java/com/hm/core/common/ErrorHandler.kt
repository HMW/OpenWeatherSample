package com.hm.core.common

import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

object ErrorHandler {
    
    fun handleException(exception: Throwable): WeatherException {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    400 -> WeatherException.ApiException(400, "無效的請求參數", exception)
                    401 -> WeatherException.ApiException(401, "API 金鑰無效或已過期", exception)
                    404 -> WeatherException.ApiException(404, "找不到指定的位置", exception)
                    429 -> WeatherException.ApiException(429, "API 請求次數超限", exception)
                    in 500..599 -> WeatherException.ApiException(exception.code(), "伺服器錯誤", exception)
                    else -> WeatherException.ApiException(exception.code(), "網路請求失敗", exception)
                }
            }
            is UnknownHostException -> WeatherException.NetworkException("網路連線失敗，請檢查網路設定", exception)
            is IOException -> WeatherException.NetworkException("網路連線異常", exception)
            is WeatherException -> exception
            else -> WeatherException.NetworkException("未知錯誤：${exception.message}", exception)
        }
    }
    
    fun getErrorMessage(exception: WeatherException): String {
        return when (exception) {
            is WeatherException.NetworkException -> "網路錯誤：${exception.message}"
            is WeatherException.DatabaseException -> "資料庫錯誤：${exception.message}"
            is WeatherException.ApiException -> "API 錯誤 (${exception.code})：${exception.message}"
            is WeatherException.CacheException -> "快取錯誤：${exception.message}"
            is WeatherException.LocationException -> "位置錯誤：${exception.message}"
        }
    }
}
