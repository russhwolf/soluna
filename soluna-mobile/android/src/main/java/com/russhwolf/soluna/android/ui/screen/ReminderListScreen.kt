package com.russhwolf.soluna.android.ui.screen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.DismissValue
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.russhwolf.soluna.android.R
import com.russhwolf.soluna.android.extensions.text
import com.russhwolf.soluna.android.ui.components.ConfirmationDialog
import com.russhwolf.soluna.android.ui.theme.SolunaTheme
import com.russhwolf.soluna.mobile.db.Reminder
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel

@Composable
fun ReminderListScreen(viewModel: ReminderListViewModel, navController: NavController) =
    Screen(
        viewModel,
        onEvent = { event ->
            when (event) {
                ReminderListViewModel.Event.Exit -> navController.navigateUp()
            }
        }
    ) { state, performAction ->
        ReminderListScreenContent(
            state = state,
            onNavigateUp = { navController.navigateUp() },
            onAddReminder = { type, minutesBefore ->
                performAction(ReminderListViewModel.Action.AddReminder(type, minutesBefore))
            },
            onSetReminderType = { id, type ->
                performAction(ReminderListViewModel.Action.SetReminderType(id, type))
            },
            onSetReminderMinutesBefore = { id, minutesBefore ->
                performAction(ReminderListViewModel.Action.SetReminderMinutesBefore(id, minutesBefore))
            },
            onSetReminderEnabled = { id, enabled ->
                performAction(ReminderListViewModel.Action.SetReminderEnabled(id, enabled))
            },
            onRemoveReminder = { id ->
                performAction(ReminderListViewModel.Action.RemoveReminder(id))
            }
        )
    }

@Composable
private fun ReminderListScreenContent(
    state: ReminderListViewModel.State,
    onNavigateUp: () -> Unit,
    onAddReminder: (type: ReminderType, minutesBefore: Int) -> Unit,
    onSetReminderType: (id: Long, type: ReminderType) -> Unit,
    onSetReminderMinutesBefore: (id: Long, minutesBefore: Int) -> Unit,
    onSetReminderEnabled: (id: Long, enabled: Boolean) -> Unit,
    onRemoveReminder: (id: Long) -> Unit
) {
    Scaffold(
        topBar = {
            ReminderListAppBar(
                onNavigateUp = onNavigateUp,
                onAddReminder = onAddReminder
            )
        }
    ) {
        LazyColumn {
            items(state.reminders) { reminder ->
                ReminderListItem(
                    onSetReminderEnabled = onSetReminderEnabled,
                    reminder = reminder,
                    onSetReminderMinutesBefore = onSetReminderMinutesBefore,
                    onSetReminderType = onSetReminderType,
                    onRemoveReminder = onRemoveReminder
                )
            }
        }
    }
}

@Composable
private fun ReminderListAppBar(
    onNavigateUp: () -> Unit,
    onAddReminder: (type: ReminderType, minutesBefore: Int) -> Unit
) {
    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringResource(R.string.action_back))
            }
        },
        title = { Text(stringResource(R.string.title_reminders), style = SolunaTheme.typography.h6) },
        actions = {
            IconButton(onClick = { onAddReminder(ReminderType.Sunset, 15) }) {
                Icon(
                    imageVector = Icons.Default.AddAlert,
                    contentDescription = stringResource(R.string.reminders_action_add)
                )
            }
        }
    )
}

@Composable
private fun ReminderListItem(
    onSetReminderEnabled: (id: Long, enabled: Boolean) -> Unit,
    reminder: Reminder,
    onSetReminderMinutesBefore: (id: Long, minutesBefore: Int) -> Unit,
    onSetReminderType: (id: Long, type: ReminderType) -> Unit,
    onRemoveReminder: (id: Long) -> Unit
) {
    val confirmDeleteReminder = remember { mutableStateOf(false) }
    val dismissState = rememberDismissState(
        confirmStateChange = {
            val confirmed = it != DismissValue.Default
            if (confirmed) {
                confirmDeleteReminder.value = true
            }
            return@rememberDismissState confirmed
        }
    )
    SwipeToDismiss(
        state = dismissState,
        background = {}
    ) {
        ListItem(
            icon = {
                Checkbox(
                    checked = reminder.enabled,
                    onCheckedChange = { onSetReminderEnabled(reminder.id, !reminder.enabled) }
                )
            },
            text = {
                ReminderListItemContent(
                    reminder = reminder,
                    onSetReminderMinutesBefore = onSetReminderMinutesBefore,
                    onSetReminderType = onSetReminderType
                )
            },
        )
    }
    Divider()

    if (confirmDeleteReminder.value) {
        ConfirmationDialog(
            confirmButtonContent = { Text(stringResource(R.string.action_delete)) },
            dismissButtonContent = { Text(stringResource(R.string.action_cancel)) },
            onConfirm = { onRemoveReminder(reminder.id) },
            onDismiss = { confirmDeleteReminder.value = false },
            content = { Text(stringResource(R.string.reminders_confirm_remove)) }
        )
    } else {
        LaunchedEffect(confirmDeleteReminder) {
            dismissState.animateTo(DismissValue.Default)
        }
    }
}

@Composable
private fun ReminderListItemContent(
    reminder: Reminder,
    onSetReminderMinutesBefore: (id: Long, minutesBefore: Int) -> Unit,
    onSetReminderType: (id: Long, type: ReminderType) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val minutesBefore =
            remember { mutableStateOf(TextFieldValue(reminder.minutesBefore.toString())) }
        val dropdownExpanded = remember { mutableStateOf(false) }

        /**
         * Overengineered rendering of the description to try to be locale-independent
         */
        @Composable
        fun DescriptionElement(string: String) {
            when (string) {
                "%1\$s" -> OutlinedTextField(
                    value = minutesBefore.value,
                    onValueChange = {
                        minutesBefore.value = it//.copy(text = it.text.take(3))

                        // TODO debounce this?
                        val newMinutesBefore = it.text.toIntOrNull()
                        if (newMinutesBefore != null && newMinutesBefore != reminder.minutesBefore && newMinutesBefore >= 0 && newMinutesBefore < 1000) {
                            onSetReminderMinutesBefore(reminder.id, newMinutesBefore)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = run {
                        val dpPerSp = LocalContext.current.resources.displayMetrics.run { scaledDensity / density }
                        Modifier
                            .wrapContentSize()
                            .width((64 * dpPerSp).dp) // TODO how to set exact EM size?
                    }
                )
                "%2\$s" -> Box {
                    TextButton(
                        onClick = { dropdownExpanded.value = true },
                        content = { Text(reminder.type.text, color = SolunaTheme.colors.onSurface, maxLines = 1) }
                    )
                    DropdownMenu(
                        expanded = dropdownExpanded.value,
                        onDismissRequest = { dropdownExpanded.value = false },
                    ) {
                        ReminderType.values().forEach { reminderType ->
                            DropdownMenuItem(onClick = {
                                onSetReminderType(reminder.id, reminderType)
                                dropdownExpanded.value = false
                            }) {
                                Text(reminderType.text)
                            }
                        }
                    }
                }
                else -> Text(string, maxLines = 1)
            }
        }
        getDescriptionStrings().forEach { DescriptionElement(it) }
    }
}

/**
 * Hacky overengineered processing of "%1$s minutes before %2$s" to try to be locale-independent
 */
@Composable
private fun getDescriptionStrings(): List<String> {
    val string = stringResource(R.string.reminders_description)
    val regex = Regex("%[0-9]+\\\$s")
    val split: List<String> = string.split(regex)
    val matches: List<String> = regex.findAll(string).flatMap { it.groupValues }.toList()
    return split.zip(matches) { a, b -> a to b }.flatMap { listOf(it.first, it.second) } + split.drop(matches.size)
}

internal class ReminderListProvider : PreviewParameterProvider<ReminderListViewModel.State> {
    override val values: Sequence<ReminderListViewModel.State> = sequenceOf(
        ReminderListViewModel.State(
            listOf(
                Reminder(0, ReminderType.Sunset, 120, true),
                Reminder(1, ReminderType.Sunrise, 15, false)
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
fun ReminderListScreenContent_Previews(
    @PreviewParameter(provider = ReminderListProvider::class)
    state: ReminderListViewModel.State
) {
    SolunaTheme {
        Surface(color = MaterialTheme.colors.background) {
            ReminderListScreenContent(
                state = state,
                onNavigateUp = {},
                onAddReminder = { _, _ -> },
                onSetReminderType = { _, _ -> },
                onSetReminderMinutesBefore = { _, _ -> },
                onSetReminderEnabled = { _, _ -> },
                onRemoveReminder = {},
            )
        }
    }
}
