package com.russhwolf.soluna.android.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.russhwolf.soluna.mobile.repository.LocationPermissionRequester
import kotlinx.coroutines.CompletableDeferred

@Composable
fun rememberLocationPermissionRequester(): LocationPermissionRequester {
    val permissionResultDeferred = remember { mutableStateOf<CompletableDeferred<Boolean>?>(null) }
    val permissionRequestLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        permissionResultDeferred.value?.complete(it)
        permissionResultDeferred.value = null
    }
    return remember {
        object : LocationPermissionRequester {
            override suspend fun requestLocationPermission(): Boolean {
                val deferred = CompletableDeferred<Boolean>()
                permissionResultDeferred.value?.cancel()
                permissionResultDeferred.value = deferred

                permissionRequestLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)

                return deferred.await()
            }
        }
    }
}
