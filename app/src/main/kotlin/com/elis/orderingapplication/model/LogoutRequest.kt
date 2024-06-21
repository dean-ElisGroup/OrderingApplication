package com.elis.orderingapplication.model

import com.google.gson.annotations.SerializedName

data class LogoutRequest (
    @SerializedName("sessionKey") val sessionKey: String?
)





