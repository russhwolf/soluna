package com.russhwolf.soluna.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.russhwolf.soluna.android.ui.screen.AddLocationScreen
import com.russhwolf.soluna.android.ui.screen.Destination
import com.russhwolf.soluna.android.ui.screen.HomeScreen
import com.russhwolf.soluna.android.ui.screen.LocationDetailScreen
import com.russhwolf.soluna.android.ui.screen.LocationListScreen
import com.russhwolf.soluna.android.ui.screen.ReminderListScreen
import com.russhwolf.soluna.android.ui.screen.SettingsScreen
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import com.russhwolf.soluna.mobile.koinUiScopeQualifier
import org.koin.androidx.compose.getKoin
import org.koin.core.scope.Scope
import org.koin.dsl.module

private const val composeScopeId = "compose"

@Composable
fun SolunaApp() {
    val locationPermissionRequester = rememberLocationPermissionRequester()
    val koin = getKoin()
    val scopeState = remember { mutableStateOf<Scope?>(null) }

    DisposableEffect(koin, locationPermissionRequester) {
        val module = module {
            scope(koinUiScopeQualifier) {
                scoped { locationPermissionRequester }
            }
        }

        val scope = koin.createScope(scopeId = composeScopeId, qualifier = koinUiScopeQualifier)
        scopeState.value = scope
        koin.loadModules(modules = listOf(module))

        onDispose {
            scope.close()
            koin.unloadModules(modules = listOf(module))
        }
    }

    // This will be null on first pass before DisposableEffect has run
    scopeState.value?.SolunaUi()
}

@Composable
fun Scope.SolunaUi() {
    SolunaTheme {
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
            composable(Destination.ReminderList) { ReminderListScreen(get(), navController) }
            composable(Destination.Settings) { SettingsScreen(get(), navController) }
        }
    }
}

