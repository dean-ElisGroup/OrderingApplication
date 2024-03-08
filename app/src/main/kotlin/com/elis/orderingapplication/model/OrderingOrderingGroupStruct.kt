package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingOrderingGroupStruct(
    @SerializedName("orderingGroupDescription")
    val orderingGroupDescription: String,
    @SerializedName("orderingGroupNo")
    val orderingGroupNo: String
)