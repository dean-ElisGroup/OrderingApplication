package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class PointsOfService(
    @SerializedName("OrderingPointOfServiceStruct")
    val orderingPointOfServiceStruct: List<OrderingPointOfServiceStruct>
)