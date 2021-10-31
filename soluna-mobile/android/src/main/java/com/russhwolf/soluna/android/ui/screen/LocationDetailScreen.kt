package com.russhwolf.soluna.android.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.russhwolf.soluna.android.R
import com.russhwolf.soluna.android.extensions.toDisplayTime
import com.russhwolf.soluna.android.ui.components.ConfirmationDialog
import com.russhwolf.soluna.android.ui.components.SunMoonTimesGraphic
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import com.russhwolf.soluna.mobile.repository.SelectableLocation
import com.russhwolf.soluna.mobile.screen.locationdetail.LocationDetailViewModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.math.abs

@Composable
fun LocationDetailScreen(viewModel: LocationDetailViewModel, navController: NavController) =
    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                LocationDetailViewModel.Event.Exit -> navController.navigateUp()
            }
        }
    ) { state, performAction ->
        LocationDetailScreenContent(
            state = state,
            onNavigateUp = { navController.navigateUp() },
            onDeleteLocation = { performAction(LocationDetailViewModel.Action.Delete) },
            onSelectLocation = { performAction(LocationDetailViewModel.Action.ToggleSelected) }
        )
    }

@Composable
private fun LocationDetailScreenContent(
    state: LocationDetailViewModel.State,
    onNavigateUp: () -> Unit,
    onDeleteLocation: () -> Unit,
    onSelectLocation: () -> Unit
) {
    Scaffold(
        topBar = {
            LocationDetailAppBar(
                state = state,
                onNavigateUp = onNavigateUp,
                onSelectLocation = onSelectLocation,
                onDeleteLocation = onDeleteLocation
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (state) {
                LocationDetailViewModel.State.Loading -> Loading()
                LocationDetailViewModel.State.InvalidLocation -> InvalidLocation()
                is LocationDetailViewModel.State.Populated -> Populated(state)
            }
        }
    }
}

@Composable
private fun LocationDetailAppBar(
    state: LocationDetailViewModel.State,
    onNavigateUp: () -> Unit,
    onSelectLocation: () -> Unit,
    onDeleteLocation: () -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
        },
        title = {
            val title = if (state is LocationDetailViewModel.State.Populated) {
                state.location.label
            } else {
                stringResource(R.string.title_locationdetail)
            }
            Text(title, style = SolunaTheme.typography.h6)
        },
        actions = {
            if (state is LocationDetailViewModel.State.Populated) {
                val confirmDeleteLocation = remember { mutableStateOf(false) }
                IconButton(onClick = { confirmDeleteLocation.value = true }) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.locations_action_delete))
                }
                IconButton(onClick = onSelectLocation) {
                    Icon(
                        if (state.location.selected) {
                            Icons.Default.RadioButtonChecked
                        } else {
                            Icons.Default.RadioButtonUnchecked
                        },
                        contentDescription = if (state.location.selected) {
                            stringResource(R.string.locationdetail_action_unselect)
                        } else {
                            stringResource(R.string.locationdetail_action_select)
                        }
                    )
                }
                if (confirmDeleteLocation.value) {
                    ConfirmationDialog(
                        confirmButtonContent = { Text(stringResource(R.string.action_delete)) },
                        dismissButtonContent = { Text(stringResource(R.string.action_cancel)) },
                        onConfirm = onDeleteLocation,
                        onDismiss = { confirmDeleteLocation.value = false },
                        content = { Text(stringResource(R.string.locations_confirm_remove, state.location.label)) }
                    )
                }
            }
        }
    )
}

@Composable
private fun ColumnScope.Loading() {
    Box(Modifier.Companion.weight(1f), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ColumnScope.InvalidLocation() {
    Box(Modifier.Companion.weight(1f), contentAlignment = Alignment.Center) {
        Text(stringResource(R.string.locationdetail_invalid))
    }
}

@Composable
private fun ColumnScope.Populated(state: LocationDetailViewModel.State.Populated) {
    LocationInfo(state)
    SunMoonTimesGraphic(
        currentTime = state.currentTime,
        sunriseTime = state.sunriseTime,
        sunsetTime = state.sunsetTime,
        moonriseTime = state.moonriseTime,
        moonsetTime = state.moonsetTime,
        timeZone = state.timeZone
    )
    Spacer(Modifier.Companion.weight(1f))
}

@Composable
private fun LocationInfo(state: LocationDetailViewModel.State.Populated) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            stringResource(
                R.string.locationdetail_latitude,
                getLocationCoordinateString(
                    state.location.latitude,
                    stringResource(R.string.location_abbr_north),
                    stringResource(R.string.location_abbr_south)
                )
            )
        )
        Text(
            stringResource(
                R.string.locationdetail_longitude,
                getLocationCoordinateString(
                    state.location.longitude,
                    stringResource(R.string.location_abbr_east),
                    stringResource(R.string.location_abbr_west)
                )
            )
        )
        Text(
            stringResource(
                R.string.locationdetail_timezone,
                state.timeZone.id
            )
        )
        Text(
            stringResource(
                R.string.locationdetail_sunrise,
                state.sunriseTime?.toDisplayTime(state.timeZone)
                    ?: stringResource(R.string.home_times_none)
            )
        )
        Text(
            stringResource(
                R.string.locationdetail_sunset,
                state.sunsetTime?.toDisplayTime(state.timeZone)
                    ?: stringResource(R.string.home_times_none)
            )
        )
        Text(
            stringResource(
                R.string.locationdetail_moonrise,
                state.moonriseTime?.toDisplayTime(state.timeZone)
                    ?: stringResource(R.string.home_times_none)
            )
        )
        Text(
            stringResource(
                R.string.locationdetail_moonset,
                state.moonsetTime?.toDisplayTime(state.timeZone)
                    ?: stringResource(R.string.home_times_none)
            )
        )
    }
}

@Composable
private fun getLocationCoordinateString(
    coordinate: Double,
    positiveDirection: String,
    negativeDirection: String
): String {
    return stringResource(
        R.string.location_format_single,
        abs(coordinate),
        if (coordinate > 0) positiveDirection else negativeDirection
    )
}

internal class LocationDetailStateProvider : PreviewParameterProvider<LocationDetailViewModel.State> {
    private val timeZone = TimeZone.of("America/New_York")

    override val values: Sequence<LocationDetailViewModel.State> = sequenceOf(
        LocationDetailViewModel.State.Loading,
        LocationDetailViewModel.State.InvalidLocation,
        LocationDetailViewModel.State.Populated(
            location = SelectableLocation(
                id = 0,
                label = "Home",
                latitude = 27.183,
                longitude = 62.832,
                timeZone = timeZone.id,
                selected = true,
            ),
            currentTime = LocalDateTime(2021, 1, 1, 11, 0).toInstant(timeZone),
            sunriseTime = LocalDateTime(2021, 1, 1, 6, 0).toInstant(timeZone),
            sunsetTime = LocalDateTime(2021, 1, 1, 20, 0).toInstant(timeZone),
            moonriseTime = LocalDateTime(2021, 1, 1, 8, 30).toInstant(timeZone),
            moonsetTime = LocalDateTime(2021, 1, 1, 22, 0).toInstant(timeZone),
            timeZone = timeZone
        )
    )
}

@Preview(showSystemUi = true)
@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
// TODO awaiting better landscape preview APIs
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 800, heightDp = 480)
@Preview(device = Devices.AUTOMOTIVE_1024p, widthDp = 800, heightDp = 480, uiMode = UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, fontScale = 2f)
@Preview(showSystemUi = true, fontScale = 2f, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun LocationDetailScreenContent_Previews(
    @PreviewParameter(provider = LocationDetailStateProvider::class)
    state: LocationDetailViewModel.State
) {
    SolunaTheme {
        LocationDetailScreenContent(
            state = state,
            onNavigateUp = {},
            onDeleteLocation = {},
            onSelectLocation = {}
        )
    }
}

