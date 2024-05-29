package com.elis.orderingapplication.pojo2

import androidx.room.Embedded
import androidx.room.Relation

data class DeliveryAddressPosJoin(
    @Embedded val deliveryAddress: DeliveryAddress,
    @Relation(
        parentColumn = "deliveryAddressNo",
        entityColumn = "deliveryAddressNo"
    )
    val pos: List<JoinOrderingGroup>?
)