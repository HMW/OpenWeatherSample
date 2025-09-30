package com.hm.core.domain.usecase

import com.hm.core.domain.model.Weather
import com.hm.core.domain.model.CurrentWeatherInfo
import javax.inject.Inject

data class WeatherAnalysis(
    val temperatureCategory: TemperatureCategory,
    val weatherCondition: WeatherCondition,
    val comfortLevel: ComfortLevel,
    val recommendation: String
)

enum class TemperatureCategory {
    FREEZING,      // 冰凍 (< 0°C)
    COLD,          // 寒冷 (0-10°C)
    COOL,          // 涼爽 (10-20°C)
    MILD,          // 溫和 (20-25°C)
    WARM,          // 溫暖 (25-30°C)
    HOT,           // 炎熱 (30-35°C)
    VERY_HOT       // 非常炎熱 (> 35°C)
}

enum class WeatherCondition {
    CLEAR,         // 晴朗
    CLOUDY,        // 多雲
    RAINY,         // 雨天
    STORMY,        // 暴風雨
    SNOWY,         // 下雪
    FOGGY          // 霧天
}

enum class ComfortLevel {
    VERY_UNCOMFORTABLE,  // 非常不舒適
    UNCOMFORTABLE,      // 不舒適
    NEUTRAL,            // 中性
    COMFORTABLE,        // 舒適
    VERY_COMFORTABLE    // 非常舒適
}

class WeatherAnalysisUseCase @Inject constructor() {
    
    fun analyzeWeather(weather: Weather): WeatherAnalysis {
        val current = weather.current
        val temperatureCategory = categorizeTemperature(current.temperature)
        val weatherCondition = categorizeWeatherCondition(current.weather.firstOrNull()?.main ?: "Clear")
        val comfortLevel = calculateComfortLevel(current.temperature, current.humidity, current.windSpeed)
        val recommendation = generateRecommendation(temperatureCategory, weatherCondition, comfortLevel)
        
        return WeatherAnalysis(
            temperatureCategory = temperatureCategory,
            weatherCondition = weatherCondition,
            comfortLevel = comfortLevel,
            recommendation = recommendation
        )
    }
    
    private fun categorizeTemperature(temperature: Double): TemperatureCategory {
        return when {
            temperature < 0 -> TemperatureCategory.FREEZING
            temperature < 10 -> TemperatureCategory.COLD
            temperature < 20 -> TemperatureCategory.COOL
            temperature < 25 -> TemperatureCategory.MILD
            temperature < 30 -> TemperatureCategory.WARM
            temperature <= 35 -> TemperatureCategory.HOT
            else -> TemperatureCategory.VERY_HOT
        }
    }
    
    private fun categorizeWeatherCondition(condition: String): WeatherCondition {
        return when (condition.lowercase()) {
            "clear" -> WeatherCondition.CLEAR
            "clouds" -> WeatherCondition.CLOUDY
            "rain", "drizzle" -> WeatherCondition.RAINY
            "thunderstorm" -> WeatherCondition.STORMY
            "snow" -> WeatherCondition.SNOWY
            "mist", "fog", "haze" -> WeatherCondition.FOGGY
            else -> WeatherCondition.CLEAR
        }
    }
    
    private fun calculateComfortLevel(temperature: Double, humidity: Int, windSpeed: Double): ComfortLevel {
        // 簡化的舒適度計算
        val heatIndex = calculateHeatIndex(temperature, humidity)
        val windChill = calculateWindChill(temperature, windSpeed)
        
        return when {
            heatIndex > 40 || windChill < -10 -> ComfortLevel.VERY_UNCOMFORTABLE
            heatIndex > 35 || windChill < -5 -> ComfortLevel.UNCOMFORTABLE
            heatIndex in 25.0..35.0 && windChill > -5 -> ComfortLevel.COMFORTABLE
            heatIndex in 20.0..25.0 && windChill > 0 -> ComfortLevel.VERY_COMFORTABLE
            else -> ComfortLevel.NEUTRAL
        }
    }
    
    private fun calculateHeatIndex(temperature: Double, humidity: Int): Double {
        // 簡化的熱指數計算
        return temperature + (humidity - 50) * 0.1
    }
    
    private fun calculateWindChill(temperature: Double, windSpeed: Double): Double {
        // 簡化的風寒指數計算
        return temperature - (windSpeed * 0.5)
    }
    
    private fun generateRecommendation(
        temperatureCategory: TemperatureCategory,
        weatherCondition: WeatherCondition,
        comfortLevel: ComfortLevel
    ): String {
        return when {
            temperatureCategory == TemperatureCategory.FREEZING -> "天氣極冷，請注意保暖，避免長時間戶外活動"
            temperatureCategory == TemperatureCategory.COLD -> "天氣寒冷，建議穿著保暖衣物"
            temperatureCategory == TemperatureCategory.HOT -> "天氣炎熱，請注意防曬和補充水分"
            temperatureCategory == TemperatureCategory.VERY_HOT -> "天氣極熱，避免戶外活動，注意中暑"
            weatherCondition == WeatherCondition.RAINY -> "有降雨，請攜帶雨具"
            weatherCondition == WeatherCondition.STORMY -> "有暴風雨，請避免戶外活動"
            weatherCondition == WeatherCondition.SNOWY -> "有降雪，請注意路面濕滑"
            comfortLevel == ComfortLevel.VERY_COMFORTABLE -> "天氣非常舒適，適合戶外活動"
            comfortLevel == ComfortLevel.COMFORTABLE -> "天氣舒適，適合一般戶外活動"
            else -> "天氣條件一般，請根據個人感受調整活動"
        }
    }
}
