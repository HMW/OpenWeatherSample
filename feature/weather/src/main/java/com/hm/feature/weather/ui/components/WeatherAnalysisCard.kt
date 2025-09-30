package com.hm.openweather.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hm.core.domain.usecase.WeatherAnalysis

@Composable
fun WeatherAnalysisCard(
    analysis: WeatherAnalysis,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "天氣分析",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 溫度分類
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "溫度分類:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = getTemperatureCategoryText(analysis.temperatureCategory),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 天氣條件
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "天氣條件:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = getWeatherConditionText(analysis.weatherCondition),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 舒適度
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "舒適度:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = getComfortLevelText(analysis.comfortLevel),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 推薦
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "建議",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = analysis.recommendation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private fun getTemperatureCategoryText(category: com.hm.core.domain.usecase.TemperatureCategory): String {
    return when (category) {
        com.hm.core.domain.usecase.TemperatureCategory.FREEZING -> "冰凍"
        com.hm.core.domain.usecase.TemperatureCategory.COLD -> "寒冷"
        com.hm.core.domain.usecase.TemperatureCategory.COOL -> "涼爽"
        com.hm.core.domain.usecase.TemperatureCategory.MILD -> "溫和"
        com.hm.core.domain.usecase.TemperatureCategory.WARM -> "溫暖"
        com.hm.core.domain.usecase.TemperatureCategory.HOT -> "炎熱"
        com.hm.core.domain.usecase.TemperatureCategory.VERY_HOT -> "非常炎熱"
    }
}

private fun getWeatherConditionText(condition: com.hm.core.domain.usecase.WeatherCondition): String {
    return when (condition) {
        com.hm.core.domain.usecase.WeatherCondition.CLEAR -> "晴朗"
        com.hm.core.domain.usecase.WeatherCondition.CLOUDY -> "多雲"
        com.hm.core.domain.usecase.WeatherCondition.RAINY -> "雨天"
        com.hm.core.domain.usecase.WeatherCondition.STORMY -> "暴風雨"
        com.hm.core.domain.usecase.WeatherCondition.SNOWY -> "下雪"
        com.hm.core.domain.usecase.WeatherCondition.FOGGY -> "霧天"
    }
}

private fun getComfortLevelText(level: com.hm.core.domain.usecase.ComfortLevel): String {
    return when (level) {
        com.hm.core.domain.usecase.ComfortLevel.VERY_UNCOMFORTABLE -> "非常不舒適"
        com.hm.core.domain.usecase.ComfortLevel.UNCOMFORTABLE -> "不舒適"
        com.hm.core.domain.usecase.ComfortLevel.NEUTRAL -> "中性"
        com.hm.core.domain.usecase.ComfortLevel.COMFORTABLE -> "舒適"
        com.hm.core.domain.usecase.ComfortLevel.VERY_COMFORTABLE -> "非常舒適"
    }
}



