package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingInfoResponse(
    @SerializedName("OrderingOrderInfoResponseStruct")
    val orderingOrderInfoResponseStruct: OrderingOrderInfoResponseStruct
)