package com.zygne.bluetooth.presentation.presenter.base

import com.zygne.bluetooth.domain.base.IDevice

interface BluetoothDevicePresenter {

    fun start()
    fun startDeviceLookUp()
    fun stopDeviceLookUp()
    fun createBond(position: Int)
    fun removeBond(position: Int)

    interface View {
        fun updateDeviceList(devices: List<IDevice>)
        fun deviceLookUpStopped()
        fun deviceLookUpStarted()
    }
}
