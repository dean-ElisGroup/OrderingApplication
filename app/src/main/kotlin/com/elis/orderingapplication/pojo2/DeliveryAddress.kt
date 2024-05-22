package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json
import javax.annotation.Generated
import javax.annotation.Nonnull

@Generated("jsonschema2pojo")
@Entity(tableName = "delivery_address")
data class DeliveryAddress (
    //@PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "delivery_address_id")
    val uId: Int = 0,

    @PrimaryKey
    @Nonnull
    @ColumnInfo(name = "delivery_address_no")
    @Json(name = "deliveryAddressNo")
    var deliveryAddressNo: String, //? = null,

    @Json(name = "deliveryAddressName")
    var deliveryAddressName: String? = null,

    //@Relation(parentColumn = "delivery_address_no",
    //    entityColumn = "deliveryAddressNo")

    @Ignore
    @Json(name = "pointsOfService")
    var pointsOfService: List<PointsOfService>? = null
)
{
    constructor(uId: Int, deliveryAddressNo: String, deliveryAddressName: String) : this(uId, deliveryAddressNo)
}
