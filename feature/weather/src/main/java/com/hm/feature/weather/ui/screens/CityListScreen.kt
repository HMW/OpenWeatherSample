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
import com.hm.core.domain.model.Location
import com.hm.core.presentation.weather.WeatherEvent
import com.hm.core.presentation.weather.WeatherUiState
import com.hm.core.presentation.weather.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityListScreen(
    onNavigateBack: () -> Unit,
    onCitySelected: (Location) -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "選擇城市",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("← 返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 搜尋框
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { query ->
                    viewModel.handleEvent(WeatherEvent.UpdateSearchQuery(query))
                    // 如果查詢不為空，觸發搜尋
                    if (query.isNotEmpty()) {
                        viewModel.handleEvent(WeatherEvent.SearchLocation(query))
                    } else {
                        viewModel.handleEvent(WeatherEvent.ClearSearchResults)
                    }
                },
                label = { Text("搜尋城市") },
                placeholder = { Text("例如：台北、東京、紐約") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when {
                uiState.isSearching -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("搜尋中...")
                        }
                    }
                }
                
                uiState.searchResults.isNotEmpty() -> {
                    Text(
                        text = "搜尋結果",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.searchResults) { location ->
                            CityItem(
                                location = location,
                                onClick = {
                                    // 直接導航到選擇的城市
                                    onCitySelected(location)
                                }
                            )
                        }
                    }
                }
                
                uiState.searchQuery.isNotEmpty() && uiState.searchResults.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🔍", style = MaterialTheme.typography.displayLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "找不到相關城市",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "請嘗試其他關鍵字",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                else -> {
                    // 預設城市列表
                    Text(
                        text = "熱門城市",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(getPopularCities()) { city ->
                            CityItem(
                                location = city,
                                onClick = {
                                    // 觸發選擇城市事件
                                    viewModel.handleEvent(WeatherEvent.SelectLocation(city))
                                    onCitySelected(city)
                                }
                            )
                        }
                    }
                }
            }
            
            // 錯誤訊息
            uiState.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun CityItem(
    location: Location,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🏙️",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = location.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                if (location.state != null) {
                    Text(
                        text = "${location.state}, ${location.country}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = location.country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Text(
                text = "→",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun getPopularCities(): List<Location> {
    return listOf(
        Location(
            name = "台北",
            latitude = 25.0330,
            longitude = 121.5654,
            country = "TW",
            state = "台北市"
        ),
        Location(
            name = "東京",
            latitude = 35.6762,
            longitude = 139.6503,
            country = "JP",
            state = "東京都"
        ),
        Location(
            name = "紐約",
            latitude = 40.7128,
            longitude = -74.0060,
            country = "US",
            state = "紐約州"
        ),
        Location(
            name = "倫敦",
            latitude = 51.5074,
            longitude = -0.1278,
            country = "GB",
            state = "英格蘭"
        ),
        Location(
            name = "巴黎",
            latitude = 48.8566,
            longitude = 2.3522,
            country = "FR",
            state = "法蘭西島"
        ),
        Location(
            name = "雪梨",
            latitude = -33.8688,
            longitude = 151.2093,
            country = "AU",
            state = "新南威爾斯州"
        ),
        Location(
            name = "新加坡",
            latitude = 1.3521,
            longitude = 103.8198,
            country = "SG",
            state = null
        ),
        Location(
            name = "香港",
            latitude = 22.3193,
            longitude = 114.1694,
            country = "HK",
            state = null
        )
    )
}
