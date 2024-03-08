package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingOrderInfoResponseStruct(
    @SerializedName("deliveryAddresses")
    val deliveryAddresses: DeliveryAddresses,
    @SerializedName("orderingGroups")
    val orderingGroups: OrderingGroups
)