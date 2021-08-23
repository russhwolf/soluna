package com.russhwolf.soluna.android.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.russhwolf.soluna.mobile.screen.settings.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel, navController: NavController) =
    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                SettingsViewModel.Event.Locations -> navController.navigate(Destination.LocationList)
                SettingsViewModel.Event.Reminders -> navController.navigate(Destination.ReminderList)
            }
        }
    ) { _, performAction ->
        Column {
            Text("Locations", Modifier.clickable { performAction(SettingsViewModel.Action.Locations) })
            Divider()
            Text("Reminders", Modifier.clickable { performAction(SettingsViewModel.Action.Reminders) })
            Divider()
        }
    }
