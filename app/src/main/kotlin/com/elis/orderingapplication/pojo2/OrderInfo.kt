package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
//@Entity(tableName = "order_info")
data class OrderInfo (
    @PrimaryKey(autoGenerate = true)
  //  @ColumnInfo(name = "order_info_id")
    val uId: Int = 0,

    @Json(name = "deliveryAddresses")
    var deliveryAddresses: List<DeliveryAddress>? = null,
    @Json(name = "orderingGroups")
    var orderingGroups: List<OrderingGroup>? = null
)
