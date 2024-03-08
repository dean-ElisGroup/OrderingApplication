package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class Orders(
    @SerializedName("OrderingOrderStruct")
    val orderingOrderStruct: OrderingOrderStruct
)