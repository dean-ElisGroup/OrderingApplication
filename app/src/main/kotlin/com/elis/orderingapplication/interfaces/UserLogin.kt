package com.elis.orderingapplication.interfaces

import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingInfoResponse
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingOrderInfoResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.utils.ApiChannels.Companion.LOGIN
import com.elis.orderingapplication.utils.ApiChannels.Companion.ORDER_INFO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserLogin {
    @Headers("Content-Type: application/json")
    @POST(LOGIN)
    suspend fun getSessionKey(
        @Body loginRequest: LoginRequest
    ): Response<OrderingLoginResponseStruct>
}

interface OrderInfo {
    @Headers("Content-Type: application/json")
    @POST(ORDER_INFO)
    suspend fun getOrderInfo(
        @Body sessionKey: OrderingRequest
    ): Response<OrderingInfoResponse>

}