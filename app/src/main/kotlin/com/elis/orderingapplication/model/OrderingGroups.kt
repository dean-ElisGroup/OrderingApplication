package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingGroups(
    @SerializedName("OrderingOrderingGroupStruct")
    val orderingOrderingGroupStruct: List<OrderingOrderingGroupStruct>
)