package com.zygne.bluetooth.domain.implementation

import com.zygne.bluetooth.domain.base.IDevice
import com.zygne.bluetooth.domain.base.IVendorMapper
import com.zygne.bluetooth.domain.uitls.Vendors

class VendorMapper : IVendorMapper {

    override fun mapVendorToDevice(device: IDevice): IDevice {

        var vendor = Vendors.ouiMap[device.oui]

        if (vendor == null) {
            vendor = ""
        }

        device.vendor = vendor

        return device
    }
}
