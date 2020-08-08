package com.zygne.bluetooth.domain.implementation

import com.zygne.bluetooth.domain.base.Connection
import com.zygne.bluetooth.domain.base.DeviceFilter
import com.zygne.bluetooth.domain.base.IDevice
import org.junit.Assert
import org.junit.Test

class DeviceFilterManagerTest {

    private val deviceFilterManager = DeviceFilterManager()

    @Test
    fun `Given a list of devices, When no filter specified, Then list should not be updated`() {
        val device1 = Device1()
        val device2 = Device2()

        var deviceList = mutableListOf(device1, device2)

        deviceFilterManager.filterDevices(deviceList)

        Assert.assertTrue(deviceList.size == 2)
    }

    @Test
    fun `Given a list of devices, When name filter specified for an existing device, Then list should only contain matched device`() {
        val device1 = Device1()
        val device2 = Device2()

        var deviceList = mutableListOf(device1, device2)
        deviceFilterManager.setFilter(DeviceFilter.NameFilter(device1.name))

        deviceFilterManager.filterDevices(deviceList)

        val remaining = deviceList[0]
        Assert.assertTrue(deviceList.size == 1)
        Assert.assertEquals(device1, remaining)
    }

    @Test
    fun `Given a list of devices, When name filter specified for non existing device, Then list should be empty`() {
        val device1 = Device1()
        val device2 = Device2()

        var deviceList = mutableListOf(device1, device2)
        deviceFilterManager.setFilter(DeviceFilter.NameFilter("Some Name"))

        deviceFilterManager.filterDevices(deviceList)

        Assert.assertTrue(deviceList.isEmpty())
    }

    class Device1 : IDevice {
        override val name: String
            get() = "Device1"
        override val address: String
            get() = "Device1"
        override val oui: String
            get() = "Device1"
        override val connection: Connection
            get() = Connection.Bluetooth

        override fun attached(): Boolean {
            return true
        }
    }

    class Device2 : IDevice {
        override val name: String
            get() = "Device2"
        override val address: String
            get() = "Device2"
        override val oui: String
            get() = "Device2"
        override val connection: Connection
            get() = Connection.Bluetooth

        override fun attached(): Boolean {
            return true
        }
    }
}
