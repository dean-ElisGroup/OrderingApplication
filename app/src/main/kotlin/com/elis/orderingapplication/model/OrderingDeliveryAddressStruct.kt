package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingDeliveryAddressStruct(
    @SerializedName("deliveryAddressName")
    val deliveryAddressName: String,
    @SerializedName("deliveryAddressNo")
    val deliveryAddressNo: Int,
    @SerializedName("pointsOfService")
    val pointsOfService: PointsOfService
)