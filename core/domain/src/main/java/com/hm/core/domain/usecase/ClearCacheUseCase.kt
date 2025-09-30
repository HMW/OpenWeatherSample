package com.hm.core.domain.usecase

import com.hm.core.common.Result
import com.hm.core.domain.repository.WeatherRepository
import javax.inject.Inject

class ClearCacheUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) : NoParamsUseCase<Result<Unit>> {
    
    override suspend fun invoke(): Result<Unit> {
        return weatherRepository.clearCache()
    }
}



