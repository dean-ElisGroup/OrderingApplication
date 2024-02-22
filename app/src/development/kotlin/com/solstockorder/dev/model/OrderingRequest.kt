package com.solstockorder.dev.model

import com.google.gson.annotations.SerializedName

data class OrderingRequest (
    @SerializedName("sessionKey") val sessionKey: String?
)