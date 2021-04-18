package com.russhwolf.soluna.android.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.russhwolf.soluna.mobile.screen.locationlist.LocationListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LocationListScreen(viewModel: LocationListViewModel, navController: NavController) =
    Screen(
        viewModel,
        navController.currentDestination,
        onEvent = { event ->
            when (event) {
                LocationListViewModel.Event.AddLocation ->
                    navController.navigate(Destination.AddLocation)
                is LocationListViewModel.Event.LocationDetails ->
                    navController.navigate(Destination.LocationDetail.path(event.locationId))
            }
        }
    ) { state, performAction ->
        Column {
            LazyColumn {
                items(state.locations.size) { index ->
                    val location = state.locations[index]
                    Row(
                        modifier = Modifier.combinedClickable(
                            onLongClick = { performAction(LocationListViewModel.Action.RemoveLocation(location.id)) },
                            onClick = { performAction(LocationListViewModel.Action.LocationDetails(location.id)) }
                        )
                    ) {
                        IconButton(
                            onClick = { performAction(LocationListViewModel.Action.ToggleLocationSelected(location.id)) }
                        ) {
                            Icon(
                                imageVector = if (location.selected) Icons.Filled.Star else Icons.Filled.StarOutline,
                                contentDescription = if (location.selected) "Unselect" else "Select"
                            )
                        }
                        Text(location.label)
                    }
                }
            }
            Button(onClick = { performAction(LocationListViewModel.Action.AddLocation) }) {
                Text("Add Location")
            }
        }
    }
