package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import javax.annotation.Generated
import javax.annotation.Nonnull

@Entity(tableName = "delivery_address")
data class DeliveryAddress (

    @PrimaryKey
    @Nonnull
    @ColumnInfo(name = "deliveryAddressNo")
    @Json(name = "deliveryAddressNo")
    var deliveryAddressNo: String, //? = null,

    @Json(name = "deliveryAddressName")
    var deliveryAddressName: String?,

    @Ignore
    @Json(name = "pointsOfService")
    var pointsOfService: List<PointsOfService>? = null
)
{    constructor() :this( "", "")
}
