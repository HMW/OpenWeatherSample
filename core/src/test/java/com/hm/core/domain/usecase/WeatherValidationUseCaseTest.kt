package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.common.WeatherException
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class WeatherValidationUseCaseTest {

    private lateinit var useCase: WeatherValidationUseCase

    @Before
    fun setup() {
        useCase = WeatherValidationUseCase()
    }

    @Test
    fun `validateCoordinates should return success for valid coordinates`() {
        // Given
        val latitude = 25.0330
        val longitude = 121.5654

        // When
        val result = useCase.validateCoordinates(latitude, longitude)

        // Then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `validateCoordinates should return error for invalid latitude too high`() {
        // Given
        val latitude = 91.0
        val longitude = 121.5654

        // When
        val result = useCase.validateCoordinates(latitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException.LocationException)
        assertTrue((result.exception as WeatherException.LocationException).message!!.contains("緯度"))
    }

    @Test
    fun `validateCoordinates should return error for invalid latitude too low`() {
        // Given
        val latitude = -91.0
        val longitude = 121.5654

        // When
        val result = useCase.validateCoordinates(latitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException.LocationException)
        assertTrue((result.exception as WeatherException.LocationException).message!!.contains("緯度"))
    }

    @Test
    fun `validateCoordinates should return error for invalid longitude too high`() {
        // Given
        val latitude = 25.0330
        val longitude = 181.0

        // When
        val result = useCase.validateCoordinates(latitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException.LocationException)
        assertTrue((result.exception as WeatherException.LocationException).message!!.contains("經度"))
    }

    @Test
    fun `validateCoordinates should return error for invalid longitude too low`() {
        // Given
        val latitude = 25.0330
        val longitude = -181.0

        // When
        val result = useCase.validateCoordinates(latitude, longitude)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException.LocationException)
        assertTrue((result.exception as WeatherException.LocationException).message!!.contains("經度"))
    }

    @Test
    fun `validateLocationQuery should return success for valid query`() {
        // Given
        val query = "Taipei"

        // When
        val result = useCase.validateLocationQuery(query)

        // Then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `validateLocationQuery should return error for blank query`() {
        // Given
        val query = ""

        // When
        val result = useCase.validateLocationQuery(query)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException.LocationException)
        assertTrue((result.exception as WeatherException.LocationException).message!!.contains("不能為空"))
    }

    @Test
    fun `validateLocationQuery should return error for query too short`() {
        // Given
        val query = "A"

        // When
        val result = useCase.validateLocationQuery(query)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException.LocationException)
        assertTrue((result.exception as WeatherException.LocationException).message!!.contains("至少需要"))
    }

    @Test
    fun `validateLocationQuery should return error for query too long`() {
        // Given
        val query = "A".repeat(101)

        // When
        val result = useCase.validateLocationQuery(query)

        // Then
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is WeatherException.LocationException)
        assertTrue((result.exception as WeatherException.LocationException).message!!.contains("不能超過"))
    }
}
