package com.zygne.bluetooth.domain.base

internal interface IDeviceFilterManager {

    fun setFilter(deviceFilter: DeviceFilter)

    fun filterDevices(devices: MutableList<IDevice>)
}
