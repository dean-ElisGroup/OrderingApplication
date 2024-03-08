package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class DeliveryAddresses(
    @SerializedName("OrderingDeliveryAddressStruct")
    val orderingDeliveryAddressStruct: OrderingDeliveryAddressStruct
)