package com.hm.openweather.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hm.core.domain.model.DailyWeatherInfo
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyForecastCard(
    dailyForecast: List<DailyWeatherInfo>,
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
                text = "5天預報",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dailyForecast.take(5).forEach { daily ->
                    DailyForecastItem(daily = daily)
                }
            }
        }
    }
}

@Composable
private fun DailyForecastItem(
    daily: DailyWeatherInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 日期
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = formatDate(daily.timestamp),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = formatDayOfWeek(daily.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // 天氣圖示
        if (daily.weather.isNotEmpty()) {
            Text(
                text = getWeatherIcon(daily.weather.first().main),
                style = MaterialTheme.typography.titleLarge
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 溫度範圍
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "${daily.temperature.max.toInt()}°",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${daily.temperature.min.toInt()}°",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // 降雨機率
        Text(
            text = "${(daily.precipitationProbability * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val formatter = SimpleDateFormat("MM/dd", Locale.getDefault())
    return formatter.format(date)
}

private fun formatDayOfWeek(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val formatter = SimpleDateFormat("EEEE", Locale.getDefault())
    return formatter.format(date)
}

private fun getWeatherIcon(condition: String): String {
    return when (condition.lowercase()) {
        "clear" -> "☀️"
        "clouds" -> "☁️"
        "rain" -> "🌧️"
        "drizzle" -> "🌦️"
        "thunderstorm" -> "⛈️"
        "snow" -> "❄️"
        "mist", "fog", "haze" -> "🌫️"
        else -> "🌤️"
    }
}
