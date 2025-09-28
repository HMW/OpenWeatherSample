package com.hm.openweather.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hm.core.domain.model.CurrentWeatherInfo

@Composable
fun WeatherCard(
    currentWeather: CurrentWeatherInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 溫度
            Text(
                text = "${currentWeather.temperature.toInt()}°C",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            
            // 體感溫度
            Text(
                text = "體感 ${currentWeather.feelsLike.toInt()}°C",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 天氣描述
            if (currentWeather.weather.isNotEmpty()) {
                val weatherCondition = currentWeather.weather.first()
                Text(
                    text = weatherCondition.description,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 詳細資訊
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherDetailItem(
                    label = "濕度",
                    value = "${currentWeather.humidity}%"
                )
                WeatherDetailItem(
                    label = "氣壓",
                    value = "${currentWeather.pressure} hPa"
                )
                WeatherDetailItem(
                    label = "風速",
                    value = "${currentWeather.windSpeed} m/s"
                )
                WeatherDetailItem(
                    label = "能見度",
                    value = "${currentWeather.visibility / 1000} km"
                )
            }
        }
    }
}

@Composable
private fun WeatherDetailItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
