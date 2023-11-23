package com.russhwolf.soluna.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
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
import org.koin.androidx.compose.getViewModel
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
            composableWithViewModelStoreOwner(Destination.Home) { viewModelStoreOwner, _ ->
                HomeScreen(
                    getViewModel(viewModelStoreOwner = viewModelStoreOwner),
                    navController
                )
            }
            composableWithViewModelStoreOwner(Destination.LocationList) { viewModelStoreOwner, _ ->
                LocationListScreen(
                    getViewModel(viewModelStoreOwner = viewModelStoreOwner),
                    navController
                )
            }
            composableWithViewModelStoreOwner(Destination.AddLocation) { viewModelStoreOwner, _ ->
                AddLocationScreen(
                    getViewModel(viewModelStoreOwner = viewModelStoreOwner),
                    navController
                )
            }
            composableWithViewModelStoreOwner(
                Destination.LocationDetail.template,
                arguments = Destination.LocationDetail.arguments
            ) { viewModelStoreOwner, backStackEntry ->
                LocationDetailScreen(
                    getViewModel(viewModelStoreOwner = viewModelStoreOwner) {
                        Destination.LocationDetail.parameters(backStackEntry.arguments)
                    },
                    navController
                )
            }
            composableWithViewModelStoreOwner(Destination.ReminderList) { viewModelStoreOwner, _ ->
                ReminderListScreen(
                    getViewModel(viewModelStoreOwner = viewModelStoreOwner),
                    navController
                )
            }
            composableWithViewModelStoreOwner(Destination.Settings) { viewModelStoreOwner, _ ->
                SettingsScreen(
                    getViewModel(viewModelStoreOwner = viewModelStoreOwner),
                    navController
                )
            }
        }
    }
}

private fun NavGraphBuilder.composableWithViewModelStoreOwner(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (ViewModelStoreOwner, NavBackStackEntry) -> Unit
) {
    composable(route, arguments, deepLinks) { navBackStackEntry ->
        val viewModelStoreOwner = LocalViewModelStoreOwner.current
        val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current
        if (viewModelStoreOwner != null) {
            // TODO previously this did ViewModelOwner(viewModelOwner, savedStateRegistryOwner) instead of
            //  viewModelStoreOwner. Do we still need to wire in savedStateRegistryOwner? Was it actually doing
            //  anything before?
            content(viewModelStoreOwner, navBackStackEntry)
        } else {
            // TODO or maybe show some error UI?
            error("Missing LocalViewModelStoreOwner!")
        }
    }
}
