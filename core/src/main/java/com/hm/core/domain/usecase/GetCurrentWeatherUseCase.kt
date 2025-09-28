package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.model.Weather
import com.hm.core.domain.repository.WeatherRepository
import javax.inject.Inject

data class GetCurrentWeatherParams(
    val latitude: Double,
    val longitude: Double
)

class GetCurrentWeatherUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : UseCase<GetCurrentWeatherParams, Result<Weather>> {
    
    override suspend fun invoke(parameters: GetCurrentWeatherParams): Result<Weather> {
        return weatherRepository.getCurrentWeather(
            latitude = parameters.latitude,
            longitude = parameters.longitude
        )
    }
}
