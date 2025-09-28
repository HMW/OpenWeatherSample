package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.model.Weather
import com.hm.core.domain.repository.WeatherRepository
import javax.inject.Inject

data class RefreshWeatherParams(
    val latitude: Double,
    val longitude: Double
)

class RefreshWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : UseCase<RefreshWeatherParams, Result<Weather>> {
    
    override suspend fun invoke(parameters: RefreshWeatherParams): Result<Weather> {
        return weatherRepository.refreshWeather(
            latitude = parameters.latitude,
            longitude = parameters.longitude
        )
    }
}
