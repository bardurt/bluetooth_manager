package com.zygne.bluetooth.domain.base

interface IDevice {

    val name: String

    val address: String

    val oui: String

    val connection: Connection

    fun attached(): Boolean
}
