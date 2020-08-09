package com.zygne.bluetooth.domain.base

interface IPermissionManager {

    fun hasRequiredPermission(): Boolean

    fun requestRequiredPermission()

    fun handlePermissionRequest(requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

    interface Listener {
        fun onPermissionGranted()
        fun onPermissionDenied()
    }
}
