package com.hm.core.data.repository

import com.hm.core.common.Result
import com.hm.core.common.WeatherException
import com.hm.core.data.datasource.local.WeatherLocalDataSource
import com.hm.core.data.datasource.remote.WeatherRemoteDataSource
import com.hm.core.domain.model.Weather
import com.hm.core.domain.model.CurrentWeatherInfo
import com.hm.core.domain.model.WeatherCondition
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class WeatherRepositoryImplTest {

    private lateinit var remoteDataSource: WeatherRemoteDataSource
    private lateinit var localDataSource: WeatherLocalDataSource
    private lateinit var repository: WeatherRepositoryImpl

    private val testWeather = Weather(
        latitude = 25.0330,
        longitude = 121.5654,
        timezone = "Asia/Taipei",
        timezoneOffset = 28800,
        current = CurrentWeatherInfo(
            timestamp = 1640995200L,
            sunrise = 1640952000L,
            sunset = 1640995200L,
            temperature = 20.0,
            feelsLike = 22.0,
            pressure = 1013,
            humidity = 65,
            dewPoint = 15.0,
            uvIndex = 5.0,
            clouds = 0,
            visibility = 10000,
            windSpeed = 3.5,
            windDirection = 180,
            windGust = 5.0,
            weather = listOf(WeatherCondition(800, "Clear", "clear sky", "01d"))
        ),
        hourly = emptyList(),
        daily = emptyList(),
        alerts = null,
        lastUpdated = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
    }

    @Test
    fun `getCurrentWeather should return cached data when valid`() = runTest {
        // Given
        val latitude = 25.0330
        val longitude = 121.5654
        
        coEvery { localDataSource.getWeather(latitude, longitude) } returns Result.Success(testWeather)
        
        // When
        val result = repository.getCurrentWeather(latitude, longitude)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(testWeather, (result as Result.Success).data)
        coVerify { localDataSource.getWeather(latitude, longitude) }
        coVerify(exactly = 0) { remoteDataSource.getWeatherData(any(), any(), any()) }
    }

    @Test
    fun `getCurrentWeather should fetch from remote when cache is invalid`() = runTest {
        // Given
        val latitude = 25.0330
        val longitude = 121.5654
        val oldWeather = testWeather.copy(lastUpdated = System.currentTimeMillis() - 7200000L) // 2 hours ago
        
        coEvery { localDataSource.getWeather(latitude, longitude) } returns Result.Success(oldWeather)
        coEvery { remoteDataSource.getWeatherData(latitude, longitude, any()) } returns Result.Success(testWeather)
        coEvery { localDataSource.saveWeather(any()) } returns Result.Success(Unit)
        
        // When
        val result = repository.getCurrentWeather(latitude, longitude)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(testWeather, (result as Result.Success).data)
        coVerify { remoteDataSource.getWeatherData(latitude, longitude, any()) }
        coVerify { localDataSource.saveWeather(testWeather) }
    }

    @Test
    fun `getCurrentWeather should handle remote error`() = runTest {
        // Given
        val latitude = 25.0330
        val longitude = 121.5654
        val exception = Exception("Network error")
        
        coEvery { localDataSource.getWeather(latitude, longitude) } returns Result.Error(exception)
        coEvery { remoteDataSource.getWeatherData(latitude, longitude, any()) } returns Result.Error(exception)
        
        // When
        val result = repository.getCurrentWeather(latitude, longitude)
        
        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException)
    }

    @Test
    fun `refreshWeather should always fetch from remote`() = runTest {
        // Given
        val latitude = 25.0330
        val longitude = 121.5654
        
        coEvery { remoteDataSource.getWeatherData(latitude, longitude, any()) } returns Result.Success(testWeather)
        coEvery { localDataSource.saveWeather(any()) } returns Result.Success(Unit)
        
        // When
        val result = repository.refreshWeather(latitude, longitude)
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(testWeather, (result as Result.Success).data)
        coVerify { remoteDataSource.getWeatherData(latitude, longitude, any()) }
        coVerify { localDataSource.saveWeather(testWeather) }
    }

    @Test
    fun `clearCache should call local data source`() = runTest {
        // Given
        coEvery { localDataSource.clearCache() } returns Result.Success(Unit)
        
        // When
        val result = repository.clearCache()
        
        // Then
        assertTrue(result is Result.Success)
        coVerify { localDataSource.clearCache() }
    }
}