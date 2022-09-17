package com.zygne.bluetooth.domain.uitls

object Vendors {
    val ouiMap = mapOf(
        "C47DCC" to Producer.ZEBRA,
        "94FB29" to Producer.ZEBRA,
        "4083DE" to Producer.ZEBRA,
        "84248D" to Producer.ZEBRA,
        "78B8D6" to Producer.ZEBRA,
        "00A0F8" to Producer.ZEBRA,
        "002368" to Producer.ZEBRA,
        "001570" to Producer.ZEBRA,
        "000512" to Producer.ZEBRA,

        "B0F1EC" to Producer.AMPAK,
        "E076D0" to Producer.AMPAK,
        "D49CDD" to Producer.AMPAK,
        "D41243" to Producer.AMPAK,
        "CCB8A8" to Producer.AMPAK,
        "CC4B73" to Producer.AMPAK,
        "C0847D" to Producer.AMPAK,
        "B00247" to Producer.AMPAK,
        "AC83F3" to Producer.AMPAK,
        "983B16" to Producer.AMPAK,
        "94A1A2" to Producer.AMPAK,
        "8CF710" to Producer.AMPAK,
        "70F754" to Producer.AMPAK,
        "704A0E" to Producer.AMPAK,
        "6CFAA7" to Producer.AMPAK,
        "6C21A2" to Producer.AMPAK,
        "442C05" to Producer.AMPAK,
        "28EDE0" to Producer.AMPAK,
        "2050E7" to Producer.AMPAK,
        "18937F" to Producer.AMPAK,
        "10D07A" to Producer.AMPAK,
        "102C6B" to Producer.AMPAK,
        "08E9F6" to Producer.AMPAK,
        "04E676" to Producer.AMPAK,
        "0022F4" to Producer.AMPAK,

        "54812D" to Producer.PAX,
        "00176F" to Producer.PAX,

        "E8D8D1" to Producer.HP,
        "3822E2" to Producer.HP,
        "B05CDA" to Producer.HP,
        "040E3C" to Producer.HP,
        "0068EB" to Producer.HP,

        "1C232C" to Producer.SAMSUNG,
        "24FCE5" to Producer.SAMSUNG,
        "74EB80" to Producer.SAMSUNG,
        "5CF6DC" to Producer.SAMSUNG,
        "5CC1D7" to Producer.SAMSUNG,
        "E406FE" to Producer.SAMSUNG,

        "0CB527" to Producer.HUAWEI,
        "A04147" to Producer.HUAWEI,

        "E85177" to Producer.QINGDAO
    )

    object Producer {
        const val SAMSUNG = "Samsung Electronics Co., Ltd"
        const val HUAWEI = "HUAWEI TECHNOLOGIES CO., LTD"
        const val HP = "HP Inc."
        const val PAX = "PAX Computer Technology(Shenzhen) Ltd."
        const val ZEBRA = "Zebra Technologies Inc"
        const val AMPAK = "AMPAK Technology Inc."
        const val QINGDAO = "Qingdao Intelligent&Precise Electronics Co.,Ltd"
    }
}
