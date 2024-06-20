package com.iscoding.test

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.BuildCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iscoding.test.premisionsutil.CameraPermissionTextProvider
import com.iscoding.test.premisionsutil.LocationPermissionTextProvider
import com.iscoding.test.premisionsutil.PermissionDialog
import com.iscoding.test.premisionsutil.PermissionUtils
import com.iscoding.test.premisionsutil.PermissionsCallback
import com.iscoding.test.premisionsutil.PhoneCallPermissionTextProvider
import com.iscoding.test.premisionsutil.RecordAudioPermissionTextProvider
import com.iscoding.test.ui.theme.TestTheme
import com.karumi.dexter.listener.PermissionDeniedResponse

class MainActivity : ComponentActivity() {
    private val permissionsToRequest = arrayOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private  var  myPermissionsDenied: List<PermissionDeniedResponse> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val viewModel: MyViewModel = viewModel()
            val dialogQueue = viewModel.visiblePermissionDialogQueue

            val multiplePermissionResultLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions(),
                onResult = { perms ->
                    permissionsToRequest.forEach { permission ->
                        viewModel.onPermissionResult(
                            permission = permission,
                            isGranted = perms[permission] == true
                        )
                    }
                }
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    PermissionUtils.requestContactsPermission(
                        context,
                        object : PermissionsCallback {
                            override fun onPermissionRequest(granted: Boolean, permissionsDenied: List<PermissionDeniedResponse>?) {
                                if (granted) {
                                    // User has granted permission
                                    Toast.makeText(
                                        context,
                                        "Thanks for accept the permission",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // User has denied permission
                                    myPermissionsDenied = permissionsDenied!!
                                    permissionsDenied?.forEach {permission ->
                                        Toast.makeText(
                                            context,
                                            "${permission.toString()} denied ",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        })

                }
                ) {
                    Text(text = "Request with Dexter Permission")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    multiplePermissionResultLauncher.launch(permissionsToRequest)
                }) {
                    Text(text = "Request Multiple Permissions")
                }
            }
//            myPermissionsDenied?.forEach {permission ->
//                PermissionDialog(
//                    permissionTextProvider = when (permission.permissionName) {
//                        Manifest.permission.CAMERA -> {
//                            CameraPermissionTextProvider()
//                        }
//
//                        Manifest.permission.RECORD_AUDIO -> {
//                            RecordAudioPermissionTextProvider()
//                        }
//
//                        Manifest.permission.CALL_PHONE -> {
//                            PhoneCallPermissionTextProvider()
//                        }
//
//                        Manifest.permission.ACCESS_FINE_LOCATION -> {
//                            LocationPermissionTextProvider()
//                        }
//
//                        Manifest.permission.ACCESS_COARSE_LOCATION -> {
//                            LocationPermissionTextProvider()
//                        }
//
//                        else -> CameraPermissionTextProvider()
//                    },
//                    isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission.permissionName),
//                    onDismiss = viewModel::dismissDialog,
//                    onOkClick = {
//                        viewModel.dismissDialog()
//                        multiplePermissionResultLauncher.launch(arrayOf(permission.permissionName))
//                    },
//                    onGoToAppSettingsClick = ::openAppSettings
//                )
//            }
            dialogQueue
                .reversed()
                .forEach { permission ->
                    if (ContextCompat.checkSelfPermission(
                            this,
                            permission
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                Manifest.permission.CAMERA -> {
                                    CameraPermissionTextProvider()
                                }

                                Manifest.permission.RECORD_AUDIO -> {
                                    RecordAudioPermissionTextProvider()
                                }

                                Manifest.permission.CALL_PHONE -> {
                                    PhoneCallPermissionTextProvider()
                                }

                                Manifest.permission.ACCESS_FINE_LOCATION -> {
                                    LocationPermissionTextProvider()
                                }

                                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                    LocationPermissionTextProvider()
                                }

                                else -> return@forEach
                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                multiplePermissionResultLauncher.launch(arrayOf(permission))
                            },
                            onGoToAppSettingsClick = ::openAppSettings
                        )
                    }

                }
        }
    }
}


fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}

