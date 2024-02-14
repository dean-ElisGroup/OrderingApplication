package com.elis.orderingapplication.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("sessionKey") val sessionKey: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?

)
