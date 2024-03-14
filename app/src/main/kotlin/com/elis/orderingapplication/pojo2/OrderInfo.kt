package com.elis.orderingapplication.pojo2

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
@Entity
class OrderInfo {
    @PrimaryKey(autoGenerate = true)
    private val uId = 0

    @Json(name = "deliveryAddresses")
    var deliveryAddresses: List<DeliveryAddress>? = null

    @Json(name = "orderingGroups")
    var orderingGroups: List<OrderingGroup>? = null
}
