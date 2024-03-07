package com.elis.orderingapplication.model


import com.google.gson.annotations.SerializedName

data class OrderingLoginResponseStruct(
    @SerializedName("email")
    val email: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("sessionKey")
    val sessionKey: String
)