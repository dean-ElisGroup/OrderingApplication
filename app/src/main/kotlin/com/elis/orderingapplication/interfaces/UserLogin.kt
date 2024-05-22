package com.elis.orderingapplication.interfaces

import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderEventResponse
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.pojo2.SendOrder
import com.elis.orderingapplication.utils.ApiChannels.Companion.LOGIN
import com.elis.orderingapplication.utils.ApiChannels.Companion.ORDER_EVENT
import com.elis.orderingapplication.utils.ApiChannels.Companion.ORDER_INFO
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Streaming

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
    ): Response<OrderInfo>
}
interface OrderEvent {
    @Headers("Content-Type: application/json")
    @POST(ORDER_EVENT)
    suspend fun sendOrderEvent(
        @Body orderEvent: OrderEvent
    ): Response<OrderEventResponse>
}