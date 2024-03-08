package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingArticleStruct(
    @SerializedName("articleDescription")
    val articleDescription: String,
    @SerializedName("articleIntervalQty")
    val articleIntervalQty: Int,
    @SerializedName("articleMaxQty")
    val articleMaxQty: Int,
    @SerializedName("articleMinQty")
    val articleMinQty: Int,
    @SerializedName("articleNo")
    val articleNo: Int,
    @SerializedName("articleSize")
    val articleSize: String,
    @SerializedName("articleTargetQty")
    val articleTargetQty: Int
)