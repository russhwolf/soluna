package com.russhwolf.soluna.android.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.russhwolf.soluna.android.ui.screen.AddLocationScreen
import com.russhwolf.soluna.android.ui.screen.Destination
import com.russhwolf.soluna.android.ui.screen.HomeScreen
import com.russhwolf.soluna.android.ui.screen.LocationDetailScreen
import com.russhwolf.soluna.android.ui.screen.LocationListScreen
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import org.koin.androidx.compose.get

@Composable
fun SolunaApp() {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            val navController = rememberNavController()
            NavHost(navController, startDestination = Destination.Home) {
                composable(Destination.Home) { HomeScreen(get(), navController) }
                composable(Destination.LocationList) { LocationListScreen(get(), navController) }
                composable(Destination.AddLocation) { AddLocationScreen(get(), navController) }
                composable(
                    Destination.LocationDetail.template,
                    arguments = Destination.LocationDetail.arguments
                ) { backStackEntry ->
                    LocationDetailScreen(
                        get { Destination.LocationDetail.parameters(backStackEntry.arguments) },
                        navController
                    )
                }
            }
        }
    }
}
