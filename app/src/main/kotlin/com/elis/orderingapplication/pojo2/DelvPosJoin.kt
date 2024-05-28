package com.elis.orderingapplication.pojo2

import androidx.lifecycle.LiveData
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

//@Entity(primaryKeys = ["deliveryAddressNo", "deliveryAddressNo"])
data class DelvPosJoin(
    @Embedded val deliveryAddress: DeliveryAddress,
    @Relation(
        parentColumn = "deliveryAddressNo",
        entityColumn = "deliveryAddressNo"
    )
    //val pos: List<PointsOfService>?
    val pos: List<JoinOrderingGroup>?
)