package com.zygne.bluetooth.domain.implementation

import com.zygne.bluetooth.domain.base.Connection
import com.zygne.bluetooth.domain.base.IDevice
import org.junit.Assert
import org.junit.Test

class VendorMapperTest {

    private val vendorMapper = VendorMapper()

    @Test
    fun `Given an unknown OUI, When mapping OUI, Then return empty string`() {

        val device = TestDevice("")

        val result = vendorMapper.mapVendorToDevice(device)

        Assert.assertTrue(result.vendor.isEmpty())
    }

    @Test
    fun `Given an known OUI, When mapping OUI, Then return a non-empty string`() {

        val device = TestDevice("40:83:DE")

        val result = vendorMapper.mapVendorToDevice(device)

        Assert.assertTrue(result.vendor.isNotEmpty())
    }

    class TestDevice(override val oui: String) : IDevice {
        var testName = ""
        override val name: String
            get() = "Device1"
        override val address: String
            get() = "Device1"
        override val connection: Connection
            get() = Connection.Bluetooth
        override var vendor: String
            get() = testName
            set(value) { testName = value }

        override fun attached(): Boolean {
            return true
        }
    }
}
