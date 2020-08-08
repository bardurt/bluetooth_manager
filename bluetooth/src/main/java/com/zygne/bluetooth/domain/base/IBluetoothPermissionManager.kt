package com.zygne.bluetooth.domain.base

interface IBluetoothPermissionManager {

    fun hasBluetoothPermission(): Boolean

    fun requestBluetoothPermission()

    fun handlePermissionRequest(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

    interface Listener{
        fun onBluetoothPermissionGranted()
        fun onBluetoothPermissionDenied()
    }
}