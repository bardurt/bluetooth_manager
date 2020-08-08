package com.zygne.bluetooth

import android.app.Activity
import android.widget.Toast
import com.zygne.bluetooth.domain.base.IBluetoothPermissionManager
import com.zygne.bluetooth.domain.implementation.BluetoothPermissionManager
import com.zygne.bluetooth.view.BluetoothDialog

class BluetoothModule(private val activity: Activity) : IBluetoothPermissionManager.Listener {

    private val bluetoothPermissioManager = BluetoothPermissionManager(activity, this)

    fun start() {

        if (!bluetoothPermissioManager.hasBluetoothPermission()) {
            bluetoothPermissioManager.requestBluetoothPermission()
        }

    }

    fun showBluetoothDialog() {
        if (bluetoothPermissioManager.hasBluetoothPermission()) {
            BluetoothDialog(activity).show()
        } else {
            bluetoothPermissioManager.requestBluetoothPermission()
        }
    }

    fun handlePermissionRequest(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        bluetoothPermissioManager.handlePermissionRequest(
            requestCode,
            permissions,
            grantResults
        )
    }

    override fun onBluetoothPermissionGranted() {}

    override fun onBluetoothPermissionDenied() {
        Toast.makeText(activity, "Permission to denied", Toast.LENGTH_LONG).show()
    }

}