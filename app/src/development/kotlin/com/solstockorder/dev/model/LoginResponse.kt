package com.solstockorder.dev.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val sessionKey: String?,
    val message: String?,
    val name: String?,
    val email: String?

)
