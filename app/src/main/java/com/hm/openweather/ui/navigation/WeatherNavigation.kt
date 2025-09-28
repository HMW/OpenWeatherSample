package com.hm.openweather.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hm.openweather.ui.screens.WeatherScreen

@Composable
fun WeatherNavigation(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "weather"
    ) {
        composable("weather") {
            WeatherScreen()
        }
    }
}
