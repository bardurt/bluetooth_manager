package com.zygne.bluetooth.domain.base

interface IDeviceFilterManager {

    fun setFilter(deviceFilter: DeviceFilter)

    fun filterDevices(devices: MutableList<IDevice>)
}
