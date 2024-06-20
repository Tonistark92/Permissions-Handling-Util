package com.iscoding.test.premisionsutil

import android.Manifest
import android.app.Activity
import android.content.Context
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener

object PermissionUtils {
    private val permissionsToRequest = mutableListOf(
        Manifest.permission.CALL_PHONE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    fun requestContactsPermission(context: Context, callback: PermissionsCallback) {
        Dexter.withActivity(context as Activity)
            .withPermissions(permissionsToRequest)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Check if user has granted all
                    if (report?.areAllPermissionsGranted() == true) {
                        callback.onPermissionRequest(true, null)
                        report.deniedPermissionResponses
                    } else {
                        callback.onPermissionRequest(false,report?.deniedPermissionResponses)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // User has denied a permission, proceed and ask them again
                    token?.continuePermissionRequest()
                }
            }).check()
    }

}

interface PermissionsCallback {

    // Pass request granted status i.e true or false

    fun onPermissionRequest(granted: Boolean, permissionsDenied: List<PermissionDeniedResponse>?)

}