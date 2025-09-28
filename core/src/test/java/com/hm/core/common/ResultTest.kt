package com.hm.core.common

import org.junit.Test
import org.junit.Assert.*

class ResultTest {

    @Test
    fun `test success result`() {
        val data = "test data"
        val result: Result<String> = Result.Success(data)
        
        assertTrue(result is Result.Success)
        if (result is Result.Success) {
            assertEquals(data, result.data)
        }
    }

    @Test
    fun `test error result`() {
        val exception = RuntimeException("Test error")
        val result: Result<String> = Result.Error(exception)
        
        assertTrue(result is Result.Error)
        if (result is Result.Error) {
            assertEquals(exception, result.exception)
        }
    }

    @Test
    fun `test loading result`() {
        val result: Result<String> = Result.Loading
        
        assertTrue(result is Result.Loading)
    }

    @Test
    fun `test result when success`() {
        val data = "test data"
        val result: Result<String> = Result.Success(data)
        
        val value = when (result) {
            is Result.Success -> result.data
            is Result.Error -> null
            is Result.Loading -> null
        }
        
        assertEquals(data, value)
    }

    @Test
    fun `test result when error`() {
        val exception = RuntimeException("Test error")
        val result: Result<String> = Result.Error(exception)
        
        val error = when (result) {
            is Result.Success -> null
            is Result.Error -> result.exception
            is Result.Loading -> null
        }
        
        assertEquals(exception, error)
    }

    @Test
    fun `test result when loading`() {
        val result: Result<String> = Result.Loading
        
        val isLoading = when (result) {
            is Result.Success -> false
            is Result.Error -> false
            is Result.Loading -> true
        }
        
        assertTrue(isLoading)
    }
}

