package com.hm.core.common

sealed class WeatherException(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class NetworkException(message: String, cause: Throwable? = null) : WeatherException(message, cause)
    class DatabaseException(message: String, cause: Throwable? = null) : WeatherException(message, cause)
    class ApiException(val code: Int, message: String, cause: Throwable? = null) : WeatherException(message, cause)
    class CacheException(message: String, cause: Throwable? = null) : WeatherException(message, cause)
    class LocationException(message: String, cause: Throwable? = null) : WeatherException(message, cause)
}



