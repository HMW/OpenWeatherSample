package com.hm.core.di

import com.hm.core.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideGetCurrentWeatherUseCase(
        weatherRepository: com.hm.core.domain.repository.WeatherRepository
    ): GetCurrentWeatherUseCase {
        return GetCurrentWeatherUseCase(weatherRepository)
    }

    @Provides
    @Singleton
    fun provideGetWeatherForecastUseCase(
        weatherRepository: com.hm.core.domain.repository.WeatherRepository
    ): GetWeatherForecastUseCase {
        return GetWeatherForecastUseCase(weatherRepository)
    }

    @Provides
    @Singleton
    fun provideRefreshWeatherUseCase(
        weatherRepository: com.hm.core.domain.repository.WeatherRepository
    ): RefreshWeatherUseCase {
        return RefreshWeatherUseCase(weatherRepository)
    }

    @Provides
    @Singleton
    fun provideLocationUseCase(): LocationUseCase {
        return LocationUseCase()
    }

    @Provides
    @Singleton
    fun provideClearCacheUseCase(
        weatherRepository: com.hm.core.domain.repository.WeatherRepository
    ): ClearCacheUseCase {
        return ClearCacheUseCase(weatherRepository)
    }

    @Provides
    @Singleton
    fun provideWeatherValidationUseCase(): WeatherValidationUseCase {
        return WeatherValidationUseCase()
    }

    @Provides
    @Singleton
    fun provideWeatherAnalysisUseCase(): WeatherAnalysisUseCase {
        return WeatherAnalysisUseCase()
    }
}
