package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingPointOfServiceStruct(
    @SerializedName("orders")
    val orders: Orders,
    @SerializedName("pointOfServiceDescription")
    val pointOfServiceDescription: String,
    @SerializedName("pointOfServiceName")
    val pointOfServiceName: String,
    @SerializedName("pointOfServiceNo")
    val pointOfServiceNo: Int,
    @SerializedName("pointOfServiceOrderingGroupNo")
    val pointOfServiceOrderingGroupNo: String
)