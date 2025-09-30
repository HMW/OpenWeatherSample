package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.model.Weather
import com.hm.core.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

data class GetWeatherForecastParams(
    val latitude: Double,
    val longitude: Double
)

class GetWeatherForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : FlowUseCase<GetWeatherForecastParams, Result<Weather>> {
    
    override fun invoke(parameters: GetWeatherForecastParams): Flow<Result<Weather>> {
        return weatherRepository.getWeatherFlow(
            latitude = parameters.latitude,
            longitude = parameters.longitude
        )
    }
}



