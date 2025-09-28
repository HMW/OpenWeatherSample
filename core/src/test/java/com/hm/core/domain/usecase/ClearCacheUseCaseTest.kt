package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.repository.WeatherRepository
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class ClearCacheUseCaseTest {

    private lateinit var weatherRepository: WeatherRepository
    private lateinit var useCase: ClearCacheUseCase

    @Before
    fun setup() {
        weatherRepository = mockk()
        useCase = ClearCacheUseCase(weatherRepository)
    }

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        coEvery { weatherRepository.clearCache() } returns Result.Success(Unit)

        // When
        val result = useCase.invoke()

        // Then
        assertTrue(result is Result.Success)
        coVerify { weatherRepository.clearCache() }
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = Exception("Database error")
        coEvery { weatherRepository.clearCache() } returns Result.Error(exception)

        // When
        val result = useCase.invoke()

        // Then
        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
        coVerify { weatherRepository.clearCache() }
    }
}
