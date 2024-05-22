package com.elis.orderingapplication.pojo2

import androidx.room.Entity

@Entity(primaryKeys = ["deliveryAddressNo", "deliveryAddressNo"])
data class DelvPosJoin(
    val deliveryAddressNo: String?,
    val deliveryAddressNoPos: String?
)