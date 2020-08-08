package com.zygne.bluetooth.domain.base

sealed class DeviceFilter {

    abstract fun canAdd(device: IDevice): Boolean

    object AllFilter : DeviceFilter() {
        override fun canAdd(device: IDevice): Boolean {
            return true
        }
    }

    data class NameFilter(private val name: String) : DeviceFilter() {
        override fun canAdd(device: IDevice): Boolean {
            return name == device.name
        }
    }

    data class AddressFilter(private val address: String) : DeviceFilter() {
        override fun canAdd(device: IDevice): Boolean {
            return address == device.address
        }
    }

    data class OuiFilter(private val oui: String) : DeviceFilter() {
        override fun canAdd(device: IDevice): Boolean {
            return oui == device.oui
        }
    }
}
