package com.hm.core.network.dto

import com.google.gson.annotations.SerializedName

data class DailyForecastResponse(
    @SerializedName("city")
    val city: DailyForecastCityDto,
    @SerializedName("cod")
    val cod: String,
    @SerializedName("message")
    val message: Double,
    @SerializedName("cnt")
    val cnt: Int,
    @SerializedName("list")
    val list: List<DailyForecastItemDto>
)

data class DailyForecastCityDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("coord")
    val coord: Coordinates,
    @SerializedName("country")
    val country: String,
    @SerializedName("population")
    val population: Int,
    @SerializedName("timezone")
    val timezone: Int
)

data class DailyForecastItemDto(
    @SerializedName("dt")
    val dt: Long,
    @SerializedName("sunrise")
    val sunrise: Long,
    @SerializedName("sunset")
    val sunset: Long,
    @SerializedName("temp")
    val temp: DailyTemperatureDto,
    @SerializedName("feels_like")
    val feelsLike: DailyFeelsLikeDto,
    @SerializedName("pressure")
    val pressure: Int,
    @SerializedName("humidity")
    val humidity: Int,
    @SerializedName("weather")
    val weather: List<WeatherConditionDto>,
    @SerializedName("speed")
    val speed: Double,
    @SerializedName("deg")
    val deg: Int,
    @SerializedName("gust")
    val gust: Double?,
    @SerializedName("clouds")
    val clouds: Int,
    @SerializedName("pop")
    val pop: Double,
    @SerializedName("rain")
    val rain: Double?,
    @SerializedName("snow")
    val snow: Double?
)

data class DailyTemperatureDto(
    @SerializedName("day")
    val day: Double,
    @SerializedName("min")
    val min: Double,
    @SerializedName("max")
    val max: Double,
    @SerializedName("night")
    val night: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("morn")
    val morn: Double
)

data class DailyFeelsLikeDto(
    @SerializedName("day")
    val day: Double,
    @SerializedName("night")
    val night: Double,
    @SerializedName("eve")
    val eve: Double,
    @SerializedName("morn")
    val morn: Double
)



