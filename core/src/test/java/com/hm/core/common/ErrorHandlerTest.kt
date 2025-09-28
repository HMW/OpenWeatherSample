package com.hm.core.common

import org.junit.Test
import org.junit.Assert.*
import retrofit2.HttpException
import java.io.IOException
import java.net.UnknownHostException

class ErrorHandlerTest {

    @Test
    fun `test handle HttpException 400`() {
        // Given
        val httpException = HttpException(retrofit2.Response.error<Any>(400, okhttp3.ResponseBody.create(null, "")))
        
        // When
        val result = ErrorHandler.handleException(httpException)
        
        // Then
        assertTrue(result is WeatherException.ApiException)
        assertEquals(400, (result as WeatherException.ApiException).code)
        assertEquals("無效的請求參數", result.message)
    }

    @Test
    fun `test handle HttpException 401`() {
        // Given
        val httpException = HttpException(retrofit2.Response.error<Any>(401, okhttp3.ResponseBody.create(null, "")))
        
        // When
        val result = ErrorHandler.handleException(httpException)
        
        // Then
        assertTrue(result is WeatherException.ApiException)
        assertEquals(401, (result as WeatherException.ApiException).code)
        assertEquals("API 金鑰無效或已過期", result.message)
    }

    @Test
    fun `test handle HttpException 404`() {
        // Given
        val httpException = HttpException(retrofit2.Response.error<Any>(404, okhttp3.ResponseBody.create(null, "")))
        
        // When
        val result = ErrorHandler.handleException(httpException)
        
        // Then
        assertTrue(result is WeatherException.ApiException)
        assertEquals(404, (result as WeatherException.ApiException).code)
        assertEquals("找不到指定的位置", result.message)
    }

    @Test
    fun `test handle HttpException 429`() {
        // Given
        val httpException = HttpException(retrofit2.Response.error<Any>(429, okhttp3.ResponseBody.create(null, "")))
        
        // When
        val result = ErrorHandler.handleException(httpException)
        
        // Then
        assertTrue(result is WeatherException.ApiException)
        assertEquals(429, (result as WeatherException.ApiException).code)
        assertEquals("API 請求次數超限", result.message)
    }

    @Test
    fun `test handle HttpException 500`() {
        // Given
        val httpException = HttpException(retrofit2.Response.error<Any>(500, okhttp3.ResponseBody.create(null, "")))
        
        // When
        val result = ErrorHandler.handleException(httpException)
        
        // Then
        assertTrue(result is WeatherException.ApiException)
        assertEquals(500, (result as WeatherException.ApiException).code)
        assertEquals("伺服器錯誤", result.message)
    }

    @Test
    fun `test handle UnknownHostException`() {
        // Given
        val exception = UnknownHostException("No internet connection")
        
        // When
        val result = ErrorHandler.handleException(exception)
        
        // Then
        assertTrue(result is WeatherException.NetworkException)
        assertEquals("網路連線失敗，請檢查網路設定", result.message)
    }

    @Test
    fun `test handle IOException`() {
        // Given
        val exception = IOException("Connection timeout")
        
        // When
        val result = ErrorHandler.handleException(exception)
        
        // Then
        assertTrue(result is WeatherException.NetworkException)
        assertEquals("網路連線異常", result.message)
    }

    @Test
    fun `test handle WeatherException`() {
        // Given
        val exception = WeatherException.NetworkException("Custom network error")
        
        // When
        val result = ErrorHandler.handleException(exception)
        
        // Then
        assertTrue(result is WeatherException.NetworkException)
        assertEquals("Custom network error", result.message)
    }

    @Test
    fun `test handle generic Exception`() {
        // Given
        val exception = Exception("Unknown error")
        
        // When
        val result = ErrorHandler.handleException(exception)
        
        // Then
        assertTrue(result is WeatherException.NetworkException)
        assertEquals("未知錯誤：Unknown error", result.message)
    }

    @Test
    fun `test getErrorMessage for NetworkException`() {
        // Given
        val exception = WeatherException.NetworkException("Network error")
        
        // When
        val message = ErrorHandler.getErrorMessage(exception)
        
        // Then
        assertEquals("網路錯誤：Network error", message)
    }

    @Test
    fun `test getErrorMessage for DatabaseException`() {
        // Given
        val exception = WeatherException.DatabaseException("Database error")
        
        // When
        val message = ErrorHandler.getErrorMessage(exception)
        
        // Then
        assertEquals("資料庫錯誤：Database error", message)
    }

    @Test
    fun `test getErrorMessage for ApiException`() {
        // Given
        val exception = WeatherException.ApiException(400, "API error")
        
        // When
        val message = ErrorHandler.getErrorMessage(exception)
        
        // Then
        assertEquals("API 錯誤 (400)：API error", message)
    }

    @Test
    fun `test getErrorMessage for CacheException`() {
        // Given
        val exception = WeatherException.CacheException("Cache error")
        
        // When
        val message = ErrorHandler.getErrorMessage(exception)
        
        // Then
        assertEquals("快取錯誤：Cache error", message)
    }

    @Test
    fun `test getErrorMessage for LocationException`() {
        // Given
        val exception = WeatherException.LocationException("Location error")
        
        // When
        val message = ErrorHandler.getErrorMessage(exception)
        
        // Then
        assertEquals("位置錯誤：Location error", message)
    }
}
