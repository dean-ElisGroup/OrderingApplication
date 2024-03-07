package com.elis.orderingapplication.interfaces

import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.utils.ApiChannels.Companion.LOGIN
import com.elis.orderingapplication.utils.ApiUrls.Companion.BASE_URL
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserLogin {
    @Headers("Content-Type: application/json")
    @POST(LOGIN)
    suspend fun getSessionKey(
        @Body loginRequest: LoginRequest): Response<OrderingLoginResponseStruct>

}