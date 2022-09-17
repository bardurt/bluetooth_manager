package com.zygne.bluetooth

import android.app.Activity
import android.widget.Toast
import com.zygne.bluetooth.domain.base.IPermissionManager
import com.zygne.bluetooth.domain.implementation.BluetoothDeviceManager
import com.zygne.bluetooth.domain.implementation.BluetoothPermissionManager
import com.zygne.bluetooth.domain.implementation.DeviceFilterManager
import com.zygne.bluetooth.presentation.ui.BluetoothDialog
import com.zygne.bluetooth.presentation.ui.BluetoothErrorDialog

class BluetoothModule(private val activity: Activity) : IPermissionManager.Listener {

    private val bluetoothPermissionManager = BluetoothPermissionManager(activity, this)
    private val bluetoothManager = BluetoothDeviceManager(activity)
    private val filterManager = DeviceFilterManager()

    fun start() {
        if (!bluetoothManager.supportsFeature()) {
            BluetoothErrorDialog(activity, activity.getString(R.string.bluetooth_not_supported_error)).show()
            return
        }

        if (bluetoothPermissionManager.hasRequiredPermission()) {
            BluetoothDialog(activity, bluetoothManager, filterManager).show()
        } else {
            bluetoothPermissionManager.requestRequiredPermission()
        }
    }

    fun stop() {
        bluetoothManager.destroy()
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

    override fun onPermissionGranted() {}

    override fun onPermissionDenied() {
        Toast.makeText(activity, activity.getString(R.string.bluetooth_permission_denied), Toast.LENGTH_LONG).show()
    }
}
