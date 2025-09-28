package com.hm.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hm.core.data.datasource.local.WeatherLocalDataSource
import com.hm.core.data.datasource.remote.WeatherRemoteDataSource
import com.hm.core.data.mapper.WeatherMapper
import com.hm.core.data.repository.WeatherRepositoryImpl
import com.hm.core.domain.repository.WeatherRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .create()
    }

    @Provides
    @Singleton
    fun provideWeatherMapper(gson: Gson): WeatherMapper {
        return WeatherMapper(gson)
    }

    @Provides
    @Singleton
    fun provideWeatherRemoteDataSource(
        api: com.hm.core.network.OpenWeatherApi,
        mapper: WeatherMapper
    ): WeatherRemoteDataSource {
        return WeatherRemoteDataSource(api, mapper)
    }

    @Provides
    @Singleton
    fun provideWeatherLocalDataSource(
        weatherDao: com.hm.core.database.dao.WeatherDao,
        mapper: WeatherMapper
    ): WeatherLocalDataSource {
        return WeatherLocalDataSource(weatherDao, mapper)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        remoteDataSource: WeatherRemoteDataSource,
        localDataSource: WeatherLocalDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(remoteDataSource, localDataSource)
    }
}
