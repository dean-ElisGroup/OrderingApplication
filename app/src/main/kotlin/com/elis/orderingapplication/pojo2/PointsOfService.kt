package com.elis.orderingapplication.pojo2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import com.squareup.moshi.Json
import io.reactivex.annotations.NonNull

@Entity(
    tableName = "points_of_service",
    primaryKeys = ["point_of_service", "deliveryAddressNo"]
)
data class PointsOfService(
    @NonNull
    @ColumnInfo(name = "point_of_service")
    @Json(name = "pointOfServiceNo")
    var pointOfServiceNo: String = "",

    @Json(name = "pointOfServiceName")
    var pointOfServiceName: String? = null,

    @Json(name = "pointOfServiceDescription")
    var pointOfServiceDescription: String? = null,

    @ColumnInfo(name = "orderingGroupNo")
    @Json(name = "pointOfServiceOrderingGroupNo")
    var pointOfServiceOrderingGroupNo: String? = null,

    @ColumnInfo(name = "orderingGroupDescription")
    var orderingGroupDescription: String? = null,

    @NonNull
    @ColumnInfo(name = "deliveryAddressNo")
    var deliveryAddressNo: String = "",

    @ColumnInfo(name = "deliveryAddressName")
    var deliveryAddressName: String? = "",

    @Ignore
    @Json(name = "orders")
    var orders: List<Order>? = null
) {
    constructor() : this("", null, null, null, null, "")
}

