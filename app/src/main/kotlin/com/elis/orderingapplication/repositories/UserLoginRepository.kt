package com.elis.orderingapplication.repositories

import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.LogoutRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.retrofit.RetroFitInstance
import com.elis.orderingapplication.utils.ApiResponse

class UserLoginRepository {
    suspend fun getUserLogin(loginRequest: LoginRequest) =
        RetroFitInstance.api.getSessionKey(loginRequest)


    suspend fun userLogout(logoutRequest: LogoutRequest) =
        RetroFitInstance.logout.logoutRequest(logoutRequest)

    //suspend fun getOrderInfo(sessionKey: OrderingRequest) =
    //    RetroFitInstance.api2.getOrderInfo(sessionKey)
    suspend fun getOrderInfo(sessionKey: OrderingRequest): ApiResponse<OrderInfo> {
        return try {
            val response = RetroFitInstance.api2.getOrderInfo(sessionKey)
            if (response.isSuccessful) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(response.message(), null)
            }
        } catch (e: Exception) {
            ApiResponse.UnknownError(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun sendOrderEvent(orderEvent: OrderEvent) =
        RetroFitInstance.orderEvent.sendOrderEvent(orderEvent)
}
