package com.russhwolf.soluna.android.ui.screen

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.russhwolf.soluna.android.R
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import com.russhwolf.soluna.mobile.screen.addlocation.AddLocationViewModel
import kotlin.math.abs

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
        AddLocationScreenContent(
            label = label,
            latitude = latitude,
            longitude = longitude,
            timeZone = timeZone,
            onNavigateUp = { navController.navigateUp() },
            onAddLocation = {
                performAction(
                    AddLocationViewModel.Action.CreateLocation(
                        label.value,
                        latitude.value,
                        longitude.value,
                        timeZone.value
                    )
                )
            },
            onGeocodeLocation = { performAction(AddLocationViewModel.Action.GeocodeLocation(it)) },
            onDeviceLocation = { performAction(AddLocationViewModel.Action.DeviceLocation) }
        )
    }
}

@Composable
private fun AddLocationScreenContent(
    label: MutableState<String>,
    latitude: MutableState<String>,
    longitude: MutableState<String>,
    timeZone: MutableState<String>,
    onNavigateUp: () -> Unit,
    onAddLocation: () -> Unit,
    onGeocodeLocation: (String) -> Unit,
    onDeviceLocation: () -> Unit
) {

    Scaffold(
        topBar = {
            AddLocationAppBar(
                label = label,
                onNavigateUp = onNavigateUp,
                onGeocodeLocation = onGeocodeLocation,
                onDeviceLocation = onDeviceLocation
            )
        }
    ) {
        AddLocationForm(
            label = label,
            latitude = latitude,
            longitude = longitude,
            timeZone = timeZone,
            onAddLocation = onAddLocation
        )
    }
}

@Composable
private fun AddLocationAppBar(
    label: MutableState<String>,
    onNavigateUp: () -> Unit,
    onGeocodeLocation: (String) -> Unit,
    onDeviceLocation: () -> Unit,
) {
    val showGeocodeDialog = remember { mutableStateOf(false) }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.action_back)
                )
            }
        },
        title = { Text(stringResource(R.string.title_addlocation), style = SolunaTheme.typography.h6) },
        actions = {
            IconButton(onClick = { showGeocodeDialog.value = true }) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.addlocation_action_geocode)
                )
            }
            IconButton(onClick = onDeviceLocation) {
                Icon(
                    Icons.Default.GpsFixed,
                    contentDescription = stringResource(R.string.addlocation_action_devicelocation)
                )
            }
        }
    )

    if (showGeocodeDialog.value) {
        GeocodeDialog(
            defaultText = label.value,
            onSubmit = { onGeocodeLocation(it) },
            onDismiss = { showGeocodeDialog.value = false }
        )
    }
}

@Composable
private fun AddLocationForm(
    label: MutableState<String>,
    latitude: MutableState<String>,
    longitude: MutableState<String>,
    timeZone: MutableState<String>,
    onAddLocation: () -> Unit
) {
    Column(Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = label.value,
            onValueChange = { label.value = it },
            label = { Text(stringResource(R.string.addlocation_input_label)) }
        )
        CoordinateInput(
            textState = latitude,
            positiveLabel = stringResource(R.string.location_abbr_north),
            negativeLabel = stringResource(R.string.location_abbr_south),
            label = { Text(stringResource(R.string.addlocation_input_latitude)) }
        )
        CoordinateInput(
            textState = longitude,
            positiveLabel = stringResource(R.string.location_abbr_east),
            negativeLabel = stringResource(R.string.location_abbr_west),
            label = { Text(stringResource(R.string.addlocation_input_longitude)) }
        )
        OutlinedTextField(
            value = timeZone.value,
            onValueChange = { timeZone.value = it },
            label = { Text(stringResource(R.string.addlocation_input_timezone)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(onClick = onAddLocation) {
                Text(stringResource(R.string.action_submit))
            }
        }
    }
}

// TODO logic in here could use a test
@Composable
private fun CoordinateInput(
    textState: MutableState<String>,
    positiveLabel: String,
    negativeLabel: String,
    label: @Composable () -> Unit
) {
    val numericalState = derivedStateOf {
        textState.value.toDoubleOrNull()
    }
    val absNumericalState = derivedStateOf {
        numericalState.value?.let { abs(it) }
    }
    val positiveTextState = derivedStateOf {
        absNumericalState.value?.toString() ?: textState.value
    }
    val isPositive = derivedStateOf {
        numericalState.value.let { it == null || it > 0 }  // bias positive if invalid
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = positiveTextState.value,
            onValueChange = { textState.value = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = label,
            singleLine = true
        )
        Column(verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isPositive.value,
                    onClick = { textState.value = positiveTextState.value }
                )
                Text(positiveLabel)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isPositive.value,
                    onClick = { textState.value = absNumericalState.value?.unaryMinus()?.toString() ?: textState.value }
                )
                Text(negativeLabel)
            }
        }
    }
}

@Composable
private fun GeocodeDialog(
    defaultText: String,
    onSubmit: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val textState = remember { mutableStateOf(defaultText) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(textState.value)
                    onDismiss()
                },
                content = { Text(stringResource(R.string.action_submit)) }
            )
        },
        title = { Text(stringResource(R.string.addlocation_action_geocode)) },
        text = {
            OutlinedTextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                label = { Text(stringResource(R.string.addlocation_input_address)) }
            )
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun AddLocationScreenContent_Portrait_Light() {
    SolunaTheme {
        AddLocationScreenContent(
            label = remember { mutableStateOf("Somewhere") },
            latitude = remember { mutableStateOf("62.831") },
            longitude = remember { mutableStateOf("-27.182") },
            timeZone = remember { mutableStateOf("") },
            onNavigateUp = {},
            onAddLocation = {},
            onGeocodeLocation = {},
            onDeviceLocation = {}
        )
    }
}

@Preview(showSystemUi = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun AddLocationScreenContent_Portrait_Dark() {
    SolunaTheme {
        AddLocationScreenContent(
            label = remember { mutableStateOf("Somewhere") },
            latitude = remember { mutableStateOf("62.831") },
            longitude = remember { mutableStateOf("-27.182") },
            timeZone = remember { mutableStateOf("") },
            onNavigateUp = {},
            onAddLocation = {},
            onGeocodeLocation = {},
            onDeviceLocation = {}
        )
    }
}
