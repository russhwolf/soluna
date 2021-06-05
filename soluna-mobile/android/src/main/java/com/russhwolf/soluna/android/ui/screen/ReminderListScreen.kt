package com.russhwolf.soluna.android.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.russhwolf.soluna.android.extensions.text
import com.russhwolf.soluna.mobile.db.ReminderType
import com.russhwolf.soluna.mobile.screen.reminderlist.ReminderListViewModel

@OptIn(ExperimentalFoundationApi::class)
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
        Column {
            LazyColumn {
                items(state.reminders.size) { index ->
                    val reminder = state.reminders[index]
                    Row(
                        modifier = Modifier.combinedClickable(
                            onLongClick = { performAction(ReminderListViewModel.Action.RemoveReminder(reminder.id)) },
                            onClick = { },
                        )
                    ) {
                        val minutesBefore =
                            remember { mutableStateOf(TextFieldValue(reminder.minutesBefore.toString())) }
                        val dropdownExpanded = remember { mutableStateOf(false) }

                        IconButton(
                            onClick = {
                                performAction(
                                    ReminderListViewModel.Action.SetReminderEnabled(
                                        reminder.id,
                                        !reminder.enabled
                                    )
                                )
                            }
                        ) {
                            Icon(
                                imageVector = if (reminder.enabled) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                                contentDescription = if (reminder.enabled) "Disable" else "Enable"
                            )
                        }
                        TextField(
                            value = minutesBefore.value,
                            onValueChange = {
                                minutesBefore.value = it

                                // TODO debounce this?
                                val newMinutesBefore = it.text.toIntOrNull()
                                if (newMinutesBefore != null && newMinutesBefore != reminder.minutesBefore && newMinutesBefore >= 0) {
                                    performAction(
                                        ReminderListViewModel.Action.SetReminderMinutesBefore(
                                            reminder.id,
                                            newMinutesBefore
                                        )
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .width(0.dp)
                        )
                        Text(" minutes before ")
                        Box {
                            Text(reminder.type.text, modifier = Modifier.clickable { dropdownExpanded.value = true })
                            DropdownMenu(
                                expanded = dropdownExpanded.value,
                                onDismissRequest = { dropdownExpanded.value = false },
                            ) {
                                ReminderType.values().forEach { reminderType ->
                                    DropdownMenuItem(onClick = {
                                        performAction(
                                            ReminderListViewModel.Action.SetReminderType(
                                                reminder.id,
                                                reminderType
                                            )
                                        )
                                        dropdownExpanded.value = false
                                    }) {
                                        Text(reminderType.text)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Button(onClick = { performAction(ReminderListViewModel.Action.AddReminder(ReminderType.Sunset, 15)) }) {
                Text("Add Reminder")
            }
        }
    }


