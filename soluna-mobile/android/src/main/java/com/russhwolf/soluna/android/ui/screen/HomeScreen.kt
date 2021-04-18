package com.russhwolf.soluna.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.russhwolf.soluna.android.toDisplayTime
import com.russhwolf.soluna.mobile.screen.home.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, navController: NavController) =
    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                HomeViewModel.Event.Locations -> navController.navigate(Destination.LocationList)
                HomeViewModel.Event.Reminders -> TODO()
            }
        }) { state, performAction ->
        Column {
            when (state) {
                HomeViewModel.State.Loading -> Text("Loading...")
                HomeViewModel.State.NoLocationSelected -> Text("No location selected!")
                is HomeViewModel.State.Populated -> {
                    Text(state.locationName)
                    Text(state.currentTime.toDisplayTime(state.timeZone))
                    Text(state.timeZone.id)
                    Text("Sunrise: ${state.sunriseTime?.toDisplayTime(state.timeZone) ?: "None"}")
                    Text("Sunset: ${state.sunsetTime?.toDisplayTime(state.timeZone) ?: "None"}")
                    Text("Moonrise: ${state.moonriseTime?.toDisplayTime(state.timeZone) ?: "None"}")
                    Text("Moonset: ${state.moonsetTime?.toDisplayTime(state.timeZone) ?: "None"}")
                }
            }
            Button(onClick = { performAction(HomeViewModel.Action.Locations) }) { Text("Locations") }
        }
    }
