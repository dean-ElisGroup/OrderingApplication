package com.elis.orderingapplication.pojo2

import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import javax.annotation.Generated

@Generated("jsonschema2pojo")
class DeliveryAddress {
    @PrimaryKey(autoGenerate = true)
    val uId = 0
    @Json(name = "deliveryAddressNo")
    var deliveryAddressNo: String? = null

    @Json(name = "deliveryAddressName")
    var deliveryAddressName: String? = null

    @Json(name = "pointsOfService")
    var pointsOfService: List<PointsOfService>? = null
}