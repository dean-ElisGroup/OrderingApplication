package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingLoginResponse(
    @SerializedName("OrderingLoginResponseStruct")
    val orderingLoginResponseStruct: OrderingLoginResponseStruct
)