package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class Articles(
    @SerializedName("OrderingArticleStruct")
    val orderingArticleStruct: List<OrderingArticleStruct>
)