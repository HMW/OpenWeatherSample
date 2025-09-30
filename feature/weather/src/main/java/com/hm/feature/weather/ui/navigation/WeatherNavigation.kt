package com.hm.openweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hm.core.domain.model.Location
import com.hm.openweather.ui.screens.WeatherScreen
import com.hm.openweather.ui.screens.CityListScreen

@Composable
fun WeatherNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "weather"
    ) {
        composable("weather") {
            WeatherScreen(
                onNavigateToCityList = {
                    navController.navigate("city_list")
                }
            )
        }
        
        composable("weather/{latitude}/{longitude}/{cityName}") { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 25.0330
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: 121.5654
            val cityName = backStackEntry.arguments?.getString("cityName") ?: "台北"
            
            WeatherScreen(
                onNavigateToCityList = {
                    navController.navigate("city_list")
                },
                selectedLocation = com.hm.core.domain.model.Location(
                    latitude = latitude,
                    longitude = longitude,
                    name = cityName,
                    country = "",
                    state = null
                )
            )
        }
        
        composable("city_list") {
            CityListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCitySelected = { location ->
                    // 選擇城市後，將城市資訊傳遞回 WeatherScreen
                    navController.navigate("weather/${location.latitude}/${location.longitude}/${location.name}")
                }
            )
        }
    }
}
