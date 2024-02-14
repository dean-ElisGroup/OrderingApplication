package com.elis.orderingapplication.interfaces

import com.elis.orderingapplication.ApiClient
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.LoginResponse
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.DeliveryAddresses
import com.elis.orderingapplication.pojo2.OrderInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OrderInfoInterface {
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("OrderInfo/")
    suspend fun getOrderInfo(@Body orderInfoRequest: OrderingRequest):
            Response<OrderInfo>

    @Headers("Content-Type: application/json")
    @POST("Login")
    suspend fun getSessionKey(@Body loginRequest: LoginRequest): Response<LoginResponse>

    companion object {
        fun getApi(): OrderInfoInterface? {
            return ApiClient.client?.create(OrderInfoInterface::class.java)
        }
    }


}

