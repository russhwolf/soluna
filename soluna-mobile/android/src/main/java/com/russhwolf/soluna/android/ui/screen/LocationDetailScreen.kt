package com.russhwolf.soluna.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.russhwolf.soluna.android.toDisplayTime
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel

@Composable
fun LocationDetailScreen(viewModel: LocationDetailViewModel, navController: NavController) =
    Screen(
        viewModel,
        navController.currentDestination,
        onEvent = { event ->
            when (event) {
                LocationDetailViewModel.Event.Exit -> navController.navigateUp()
            }
        }
    ) { state, performAction ->
        Column {
            when (state) {
                LocationDetailViewModel.State.Loading -> Text("Loading...")
                LocationDetailViewModel.State.InvalidLocation -> Text("Location is invalid!")
                is LocationDetailViewModel.State.Populated -> {
                    Text(state.location.label)
                    Text("Latitude: ${state.location.latitude}")
                    Text("Longitude: ${state.location.longitude}")
                    Text("Time Zone: ${state.timeZone.id}")
                    Text("Sunrise: ${state.sunriseTime?.toDisplayTime(state.timeZone) ?: "None"}")
                    Text("Sunset: ${state.sunsetTime?.toDisplayTime(state.timeZone) ?: "None"}")
                    Text("Moonrise: ${state.moonriseTime?.toDisplayTime(state.timeZone) ?: "None"}")
                    Text("Moonset: ${state.moonsetTime?.toDisplayTime(state.timeZone) ?: "None"}")
                    Button(onClick = { performAction(LocationDetailViewModel.Action.Delete) }) {
                        Text("Delete")
                    }
                    IconButton(
                        onClick = { performAction(LocationDetailViewModel.Action.ToggleSelected) }
                    ) {
                        Icon(
                            imageVector = if (state.location.selected) Icons.Filled.Star else Icons.Filled.StarOutline,
                            contentDescription = if (state.location.selected) "Unselect" else "Select"
                        )
                    }

                }
            }
        }
    }
