package com.russhwolf.soluna.android.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.NavController
import com.russhwolf.soluna.android.R
import com.russhwolf.soluna.android.ui.components.ConfirmationDialog
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import com.russhwolf.soluna.mobile.repository.SelectableLocationSummary
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel

@Composable
fun LocationListScreen(viewModel: LocationListViewModel, navController: NavController) =
    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                LocationListViewModel.Event.AddLocation ->
                    navController.navigate(Destination.AddLocation)
                is LocationListViewModel.Event.LocationDetails ->
                    navController.navigate(Destination.LocationDetail.path(event.locationId))
            }
        }
    ) { state, performAction ->
        LocationListScreenContent(
            state = state,
            onNavigateUp = { navController.navigateUp() },
            onAddLocation = { performAction(LocationListViewModel.Action.AddLocation) },
            onLocationDetails = { performAction(LocationListViewModel.Action.LocationDetails(it)) },
            onSelectLocation = { performAction(LocationListViewModel.Action.ToggleLocationSelected(it)) },
            onRemoveLocation = { performAction(LocationListViewModel.Action.RemoveLocation(it)) }
        )
    }

@Composable
private fun LocationListScreenContent(
    state: LocationListViewModel.State,
    onNavigateUp: () -> Unit,
    onAddLocation: () -> Unit,
    onLocationDetails: (Long) -> Unit,
    onSelectLocation: (Long) -> Unit,
    onRemoveLocation: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            LocationListAppBar(
                onNavigateUp = onNavigateUp,
                onAddLocation = onAddLocation
            )
        }
    ) { paddingValues ->
        LazyColumn(Modifier.padding(paddingValues)) {
            items(state.locations) { location ->
                LocationListItem(
                    location = location,
                    onLocationDetails = onLocationDetails,
                    onSelectLocation = onSelectLocation,
                    onRemoveLocation = onRemoveLocation
                )
            }
        }
    }
}

@Composable
private fun LocationListItem(
    location: SelectableLocationSummary,
    onLocationDetails: (Long) -> Unit,
    onSelectLocation: (Long) -> Unit,
    onRemoveLocation: (Long) -> Unit
) {
    val confirmDeleteLocation = remember { mutableStateOf(false) }
    val dismissState = rememberDismissState(
        confirmStateChange = {
            val confirmed = it != DismissValue.Default
            if (confirmed) {
                confirmDeleteLocation.value = true
            }
            return@rememberDismissState confirmed
        }
    )
    SwipeToDismiss(
        state = dismissState,
        background = {}
    ) {
        ListItem(
            Modifier.clickable { onLocationDetails(location.id) },
            icon = {
                RadioButton(
                    selected = location.selected,
                    onClick = { onSelectLocation(location.id) },
                )
            },
            text = { Text(location.label) },
        )
    }
    Divider()

    if (confirmDeleteLocation.value) {
        ConfirmationDialog(
            confirmButtonContent = { Text(stringResource(R.string.action_delete)) },
            dismissButtonContent = { Text(stringResource(R.string.action_cancel)) },
            onConfirm = { onRemoveLocation(location.id) },
            onDismiss = { confirmDeleteLocation.value = false },
            content = { Text(stringResource(R.string.locations_confirm_remove, location.label)) }
        )
    } else {
        LaunchedEffect(confirmDeleteLocation) {
            dismissState.animateTo(DismissValue.Default)
        }
    }
}

@Composable
private fun LocationListAppBar(onNavigateUp: () -> Unit, onAddLocation: () -> Unit) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
        },
        title = { Text(stringResource(R.string.title_locations), style = SolunaTheme.typography.h6) },
        actions = {
            IconButton(onClick = onAddLocation) {
                Icon(
                    imageVector = Icons.Default.AddLocation,
                    contentDescription = stringResource(R.string.locations_action_add)
                )
            }
        }
    )
}

internal class LocationListProvider : PreviewParameterProvider<LocationListViewModel.State> {
    override val values: Sequence<LocationListViewModel.State> = sequenceOf(
        LocationListViewModel.State(
            listOf(
                SelectableLocationSummary(0, "Home", selected = true),
                SelectableLocationSummary(1, "Away", selected = false)
            )
        )
    )
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
// TODO awaiting better landscape preview APIs
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 800, heightDp = 480)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 800, heightDp = 480, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, fontScale = 2f)
@Preview(showSystemUi = true, fontScale = 2f, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LocationListScreenContent_Previews(
    @PreviewParameter(provider = LocationListProvider::class)
    state: LocationListViewModel.State
) {
    SolunaTheme {
        LocationListScreenContent(
            state = state,
            onNavigateUp = {},
            onAddLocation = {},
            onLocationDetails = {},
            onSelectLocation = {},
            onRemoveLocation = {}
        )
    }
}
