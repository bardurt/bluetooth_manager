package com.zygne.bluetooth

import android.app.Activity
import android.widget.Toast
import com.zygne.bluetooth.domain.base.DeviceFilter
import com.zygne.bluetooth.domain.base.IPermissionManager
import com.zygne.bluetooth.domain.implementation.DeviceFilterManager
import com.zygne.bluetooth.domain.implementation.DeviceManager
import com.zygne.bluetooth.domain.implementation.PermissionManager
import com.zygne.bluetooth.presentation.ui.BluetoothDialog

class BluetoothModule(private val activity: Activity) : IPermissionManager.Listener {

    private val bluetoothPermissionManager = PermissionManager(activity, this)
    private val bluetoothManager = DeviceManager(activity)
    private val filterManager = DeviceFilterManager()

    fun start() {
        filterManager.setFilter(DeviceFilter.OuiFilter("40:83:DE"))
        if (!bluetoothPermissionManager.hasRequiredPermission()) {
            bluetoothPermissionManager.requestRequiredPermission()
        }
    }

    fun showBluetoothDialog() {
        if (bluetoothPermissionManager.hasRequiredPermission()) {
            BluetoothDialog(activity, bluetoothManager, filterManager).show()
        } else {
            bluetoothPermissionManager.requestRequiredPermission()
        }
    }

    fun handlePermissionRequest(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        bluetoothPermissionManager.handlePermissionRequest(
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
