package com.zygne.bluetooth.domain.implementation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.zygne.bluetooth.domain.base.IPermissionManager

class PermissionManager(
    private val activity: Activity,
    private val listener: IPermissionManager.Listener
) : IPermissionManager {

    override fun hasRequiredPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun requestRequiredPermission() {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), BLUETOOTH_PERMISSION
        )
    }

    override fun handlePermissionRequest(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            BLUETOOTH_PERMISSION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    listener.onBluetoothPermissionGranted()
                } else {
                    listener.onBluetoothPermissionDenied()
                }
                return
            }
        }
    }

    companion object {
        private const val BLUETOOTH_PERMISSION = 10012
    }
}
