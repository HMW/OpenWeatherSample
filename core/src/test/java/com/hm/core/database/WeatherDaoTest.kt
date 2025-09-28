package com.hm.core.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hm.core.database.dao.WeatherDao
import com.hm.core.database.entity.WeatherEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

@RunWith(AndroidJUnit4::class)
class WeatherDaoTest {

    private lateinit var database: WeatherDatabase
    private lateinit var weatherDao: WeatherDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeatherDatabase::class.java
        ).allowMainThreadQueries().build()
        weatherDao = database.weatherDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetWeather() = runTest {
        val weather = WeatherEntity(
            id = "test_id",
            latitude = 25.0330,
            longitude = 121.5654,
            timezone = "Asia/Taipei",
            currentWeather = "{}",
            hourlyForecast = "[]",
            dailyForecast = "[]",
            lastUpdated = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)
        val result = weatherDao.getWeatherByLocation(25.0330, 121.5654).first()

        assertNotNull(result)
        assertEquals("test_id", result?.id)
        assertEquals(25.0330, result?.latitude ?: 0.0, 0.001)
        assertEquals(121.5654, result?.longitude ?: 0.0, 0.001)
        assertEquals("Asia/Taipei", result?.timezone)
    }

    @Test
    fun updateWeather() = runTest {
        val weather = WeatherEntity(
            id = "test_id",
            latitude = 25.0330,
            longitude = 121.5654,
            timezone = "Asia/Taipei",
            currentWeather = "{}",
            hourlyForecast = "[]",
            dailyForecast = "[]",
            lastUpdated = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)
        
        val updatedWeather = weather.copy(
            timezone = "Asia/Shanghai",
            lastUpdated = System.currentTimeMillis() + 1000
        )
        
        weatherDao.updateWeather(updatedWeather)
        val result = weatherDao.getWeatherByLocation(25.0330, 121.5654).first()

        assertNotNull(result)
        assertEquals("Asia/Shanghai", result?.timezone)
    }

    @Test
    fun deleteWeather() = runTest {
        val weather = WeatherEntity(
            id = "test_id",
            latitude = 25.0330,
            longitude = 121.5654,
            timezone = "Asia/Taipei",
            currentWeather = "{}",
            hourlyForecast = "[]",
            dailyForecast = "[]",
            lastUpdated = System.currentTimeMillis()
        )

        weatherDao.insertWeather(weather)
        weatherDao.deleteWeather(weather)
        val result = weatherDao.getWeatherByLocation(25.0330, 121.5654).first()

        assertNull(result)
    }

    @Test
    fun deleteOldWeather() = runTest {
        val currentTime = System.currentTimeMillis()
        val oldTime = currentTime - 86400000L // 24 hours ago

        val oldWeather = WeatherEntity(
            id = "old_id",
            latitude = 25.0330,
            longitude = 121.5654,
            timezone = "Asia/Taipei",
            currentWeather = "{}",
            hourlyForecast = "[]",
            dailyForecast = "[]",
            lastUpdated = oldTime
        )

        val newWeather = WeatherEntity(
            id = "new_id",
            latitude = 25.0330,
            longitude = 121.5654,
            timezone = "Asia/Taipei",
            currentWeather = "{}",
            hourlyForecast = "[]",
            dailyForecast = "[]",
            lastUpdated = currentTime
        )

        weatherDao.insertWeather(oldWeather)
        weatherDao.insertWeather(newWeather)
        
        weatherDao.deleteOldWeather(currentTime - 3600000L) // Delete weather older than 1 hour
        
        val result = weatherDao.getWeatherByLocation(25.0330, 121.5654).first()
        assertNotNull(result)
        assertEquals("new_id", result?.id)
    }
}

