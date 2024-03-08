package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingOrderStruct(
    @SerializedName("articles")
    val articles: Articles,
    @SerializedName("deliveryDate")
    val deliveryDate: String,
    @SerializedName("orderDate")
    val orderDate: String,
    @SerializedName("orderStatus")
    val orderStatus: Int,
    @SerializedName("orderType")
    val orderType: String
)