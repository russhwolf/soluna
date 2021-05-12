package com.russhwolf.soluna.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel

@Composable
fun AddLocationScreen(viewModel: AddLocationViewModel, navController: NavController) {
    val label = remember { mutableStateOf("") }
    val latitude = remember { mutableStateOf("") }
    val longitude = remember { mutableStateOf("") }
    val timeZone = remember { mutableStateOf("") }

    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                AddLocationViewModel.Event.Exit -> navController.navigateUp()
                is AddLocationViewModel.Event.ShowGeocodeData -> {
                    latitude.value = event.latitude.toString()
                    longitude.value = event.longitude.toString()
                    timeZone.value = event.timeZone
                }
            }
        }
    ) { _, performAction ->
        Column {
            TextField(value = label.value, onValueChange = { label.value = it })
            TextField(value = latitude.value, onValueChange = { latitude.value = it })
            TextField(value = longitude.value, onValueChange = { longitude.value = it })
            TextField(value = timeZone.value, onValueChange = { timeZone.value = it })
            Button(onClick = {
                performAction(
                    AddLocationViewModel.Action.CreateLocation(
                        label.value,
                        latitude.value,
                        longitude.value,
                        timeZone.value
                    )
                )
            }) {
                Text("Submit")
            }
            Button(onClick = {
                performAction(AddLocationViewModel.Action.GeocodeLocation(label.value))
            }) {
                Text("Geocode")
            }
            Button(onClick = {
                performAction(AddLocationViewModel.Action.DeviceLocation)
            }) {
                Text("Gps")
            }
        }
    }
}
