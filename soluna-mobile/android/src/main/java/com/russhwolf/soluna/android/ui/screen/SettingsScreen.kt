package com.russhwolf.soluna.android.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.russhwolf.soluna.android.R
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
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
        SettingsScreenContent(
            onNavigateUp = { navController.navigateUp() },
            onLocationsClick = { performAction(SettingsViewModel.Action.Locations) },
            onRemindersClick = { performAction(SettingsViewModel.Action.Reminders) }
        )
    }

@Composable
private fun SettingsScreenContent(
    onNavigateUp: () -> Unit,
    onLocationsClick: () -> Unit,
    onRemindersClick: () -> Unit
) {
    Scaffold(
        topBar = { SettingsAppBar(onNavigateUp = onNavigateUp) }
    ) {
        SettingsList(
            onLocationsClick = onLocationsClick,
            onRemindersClick = onRemindersClick
        )
    }
}

@Composable
private fun SettingsAppBar(onNavigateUp: () -> Unit) {
    TopAppBar(
        title = { Text(stringResource(R.string.title_settings), style = SolunaTheme.typography.h6) },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
        }
    )
}

@Composable
private fun SettingsList(onLocationsClick: () -> Unit, onRemindersClick: () -> Unit) {
    Column {
        ListItem(Modifier.clickable { onLocationsClick() }) {
            Text(stringResource(R.string.settings_action_locations))
        }
        Divider()
        ListItem(Modifier.clickable { onRemindersClick() }) {
            Text(stringResource(R.string.settings_action_reminders))
        }
        Divider()
    }
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
// TODO awaiting better landscape preview APIs
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 800, heightDp = 480)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 800, heightDp = 480, uiMode = UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, fontScale = 2f)
@Preview(showSystemUi = true, fontScale = 2f, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun SettingsScreenContent_Previews() {
    SolunaTheme {
        SettingsScreenContent(
            onNavigateUp = {},
            onLocationsClick = {},
            onRemindersClick = {}
        )
    }
}
