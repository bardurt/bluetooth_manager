package com.zygne.bluetooth.domain.implementation

import com.zygne.bluetooth.domain.base.DeviceFilter
import com.zygne.bluetooth.domain.base.IDevice
import com.zygne.bluetooth.domain.base.IDeviceFilterManager

class DeviceFilterManager : IDeviceFilterManager {

    private var filter: DeviceFilter = DeviceFilter.AllFilter

    override fun setFilter(deviceFilter: DeviceFilter) {
        filter = deviceFilter
    }

    override fun filterDevices(devices: MutableList<IDevice>) {
        devices.removeAll { !filter.canAdd(it) }
    }
}
