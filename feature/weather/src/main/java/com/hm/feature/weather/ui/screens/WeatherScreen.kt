package com.hm.openweather.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hm.core.presentation.weather.WeatherEvent
import com.hm.core.presentation.weather.WeatherUiState
import com.hm.core.presentation.weather.WeatherViewModel
import com.hm.openweather.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onNavigateToCityList: () -> Unit = {},
    selectedLocation: com.hm.core.domain.model.Location? = null,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 處理選擇的城市
    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { location ->
            viewModel.handleEvent(WeatherEvent.SelectLocation(location))
        }
    }
    
    // 如果沒有選擇城市，載入預設天氣
    LaunchedEffect(Unit) {
        if (selectedLocation == null) {
            viewModel.handleEvent(WeatherEvent.LoadWeather)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "天氣預報",
                            fontWeight = FontWeight.Bold
                        )
                        // 顯示當前城市名稱
                        uiState.location?.let { location ->
                            Text(
                                text = location.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    // 城市選擇按鈕
                    TextButton(
                        onClick = onNavigateToCityList,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("🏙️", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "選擇城市",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    
                    // 刷新按鈕
                    IconButton(
                        onClick = { viewModel.handleEvent(WeatherEvent.RefreshWeather) }
                    ) {
                        if (uiState.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("🔄", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("載入天氣資料中...")
                    }
                }
            }
            
            uiState.error != null -> {
                ErrorScreen(
                    error = uiState.error ?: "未知錯誤",
                    onRetry = { viewModel.handleEvent(WeatherEvent.LoadWeather) },
                    onDismiss = { viewModel.handleEvent(WeatherEvent.ClearError) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            uiState.weather != null -> {
                WeatherContent(
                    uiState = uiState,
                    onRefresh = { viewModel.handleEvent(WeatherEvent.RefreshWeather) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            
            else -> {
                EmptyScreen(
                    onLoadWeather = { viewModel.handleEvent(WeatherEvent.LoadWeather) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun WeatherContent(
    uiState: WeatherUiState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 當前天氣卡片
        uiState.weather?.let { weather ->
            item {
                WeatherCard(
                    currentWeather = weather.current,
                    cityName = uiState.location?.name
                )
            }
            
            // 天氣分析卡片
            uiState.analysis?.let { analysis ->
                item {
                    WeatherAnalysisCard(analysis = analysis)
                }
            }
            
            // 24小時預報
            if (weather.hourly.isNotEmpty()) {
                item {
                    HourlyForecastCard(hourlyForecast = weather.hourly)
                }
            }
            
            // 5天預報
            if (weather.daily.isNotEmpty()) {
                item {
                    DailyForecastCard(dailyForecast = weather.daily)
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "⚠️",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "載入失敗",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = error ?: "未知錯誤",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss
            ) {
                Text("關閉")
            }
            
            Button(
                onClick = onRetry
            ) {
                Text("重試")
            }
        }
    }
}

@Composable
private fun EmptyScreen(
    onLoadWeather: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🌤️",
            style = MaterialTheme.typography.displayLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "歡迎使用天氣預報",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "點擊下方按鈕開始查看天氣資訊",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onLoadWeather
        ) {
            Text("載入天氣")
        }
    }
}
