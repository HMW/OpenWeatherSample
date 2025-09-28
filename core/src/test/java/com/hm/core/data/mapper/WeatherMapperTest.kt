package com.hm.core.data.mapper

import com.google.gson.Gson
import com.hm.core.database.entity.WeatherEntity
import com.hm.core.domain.model.*
import com.hm.core.network.dto.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class WeatherMapperTest {

    private lateinit var mapper: WeatherMapper

    @Before
    fun setup() {
        mapper = WeatherMapper(Gson())
    }

    @Test
    fun `mapToDomain should correctly map current weather and forecast to domain model`() {
        // Given
        val currentWeather = createMockCurrentWeather()
        val forecast = createMockForecast()

        // When
        val result = mapper.mapToDomain(currentWeather, forecast)

        // Then
        assertEquals(25.0330, result.latitude, 0.001)
        assertEquals(121.5654, result.longitude, 0.001)
        assertEquals("UTC", result.timezone)
        assertEquals(28800, result.timezoneOffset)
        assertNotNull(result.current)
        assertTrue(result.hourly.isNotEmpty())
        assertTrue(result.daily.isNotEmpty())
        assertNull(result.alerts)
    }

    @Test
    fun `mapToEntity should correctly map domain model to entity`() {
        // Given
        val weather = createMockWeather()

        // When
        val result = mapper.mapToEntity(weather)

        // Then
        assertEquals("25.033_121.5654", result.id)
        assertEquals(25.0330, result.latitude, 0.001)
        assertEquals(121.5654, result.longitude, 0.001)
        assertEquals("Asia/Taipei", result.timezone)
        assertNotNull(result.currentWeather)
        assertNotNull(result.hourlyForecast)
        assertNotNull(result.dailyForecast)
        assertTrue(result.currentWeather.isNotEmpty())
        assertTrue(result.hourlyForecast.isNotEmpty())
        assertTrue(result.dailyForecast.isNotEmpty())
    }

    @Test
    fun `mapToDomain from entity should correctly map entity to domain model`() {
        // Given
        val weather = createMockWeather()
        val entity = mapper.mapToEntity(weather)

        // When
        val result = mapper.mapToDomain(entity)

        // Then
        assertEquals(weather.latitude, result.latitude, 0.001)
        assertEquals(weather.longitude, result.longitude, 0.001)
        assertEquals(weather.timezone, result.timezone)
        assertNotNull(result.current)
        assertTrue(result.hourly.isNotEmpty())
        assertTrue(result.daily.isNotEmpty())
    }

    private fun createMockCurrentWeather(): CurrentWeatherResponse {
        return CurrentWeatherResponse(
            coord = Coordinates(121.5654, 25.0330),
            weather = listOf(
                WeatherConditionDto(800, "Clear", "clear sky", "01d")
            ),
            base = "stations",
            main = MainWeatherDto(20.0, 22.0, 18.0, 25.0, 1013, 65, null, null),
            visibility = 10000,
            wind = WindDto(3.5, 180, 5.0),
            clouds = CloudsDto(0),
            dt = 1640995200L,
            sys = SysDto(2, 2031790, "TW", 1640952000L, 1640995200L),
            timezone = 28800,
            id = 1668341,
            name = "Taipei",
            cod = 200
        )
    }

    private fun createMockForecast(): ForecastResponse {
        return ForecastResponse(
            cod = "200",
            message = 0,
            cnt = 40,
            list = listOf(
                ForecastItemDto(
                    dt = 1640995200L,
                    main = MainWeatherDto(20.0, 22.0, 18.0, 25.0, 1013, 65, null, null),
                    weather = listOf(WeatherConditionDto(800, "Clear", "clear sky", "01d")),
                    clouds = CloudsDto(0),
                    wind = WindDto(3.5, 180, 5.0),
                    visibility = 10000,
                    pop = 0.1,
                    sys = ForecastSysDto("d"),
                    dtTxt = "2022-01-01 12:00:00"
                )
            ),
            city = CityDto(
                id = 1668341,
                name = "Taipei",
                coord = Coordinates(121.5654, 25.0330),
                country = "TW",
                population = 7871900,
                timezone = 28800,
                sunrise = 1640952000L,
                sunset = 1640995200L
            )
        )
    }

    private fun createMockWeather(): Weather {
        return Weather(
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
            hourly = listOf(
                HourlyWeatherInfo(
                    timestamp = 1640995200L,
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
                    weather = listOf(WeatherCondition(800, "Clear", "clear sky", "01d")),
                    precipitationProbability = 0.1
                )
            ),
            daily = listOf(
                DailyWeatherInfo(
                    timestamp = 1640995200L,
                    sunrise = 1640952000L,
                    sunset = 1640995200L,
                    temperature = TemperatureRange(20.0, 18.0, 25.0, 15.0, 18.0, 16.0),
                    feelsLike = FeelsLikeRange(22.0, 16.0, 18.0, 17.0),
                    pressure = 1013,
                    humidity = 65,
                    dewPoint = 15.0,
                    windSpeed = 3.5,
                    windDirection = 180,
                    weather = listOf(WeatherCondition(800, "Clear", "clear sky", "01d")),
                    clouds = 0,
                    precipitationProbability = 0.1,
                    uvIndex = 5.0
                )
            ),
            alerts = null,
            lastUpdated = System.currentTimeMillis()
        )
    }
}
