package com.iscoding.test

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultCaller
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MyViewModel : ViewModel() {
    val visiblePermissionDialogQueue = mutableStateListOf<String>()

    fun dismissDialog() {
        if (visiblePermissionDialogQueue.isNotEmpty()) {
            visiblePermissionDialogQueue.removeFirst()
            Log.d("ISLAM", "Permission dismissed")
        }
    }

    fun onPermissionResult(permission: String, isGranted: Boolean) {
        if (!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            Log.d("ISLAM", "Permission declined $permission")
            visiblePermissionDialogQueue.add(permission)
        } else {
            Log.d("ISLAM", "Permission accepted $permission")
        }
    }
    // to filter the permissions for sdk levels
    fun filterLocationPermissions(
    ):MutableList<String> {
        val permissionsBasicForLocation = mutableListOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) {
            permissionsBasicForLocation.addAll(listOf(
                android.Manifest.permission.FOREGROUND_SERVICE,
            ))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
            permissionsBasicForLocation.addAll(listOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
            ))
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ) {
            permissionsBasicForLocation.addAll(listOf(
                android.Manifest.permission.FOREGROUND_SERVICE_LOCATION,
            ))
        }
        return permissionsBasicForLocation

    }

}
