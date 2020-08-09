package com.zygne.bluetooth.domain.base

interface IVendorMapper {

    fun mapVendorToDevice(device: IDevice): IDevice
}
