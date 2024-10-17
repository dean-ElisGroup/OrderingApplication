package com.elis.orderingapplication.repositories

import com.elis.orderingapplication.utils.NetworkErrorException
import android.util.Log
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.LogoutRequest
import com.elis.orderingapplication.model.OrderingLoginResponseStruct
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.pojo2.OrderInfo
import com.elis.orderingapplication.retrofit.RetroFitInstance
import com.elis.orderingapplication.utils.ApiResponse
import retrofit2.Response

class AppRepository {
    suspend fun getUserLogin(loginRequest: LoginRequest): Response<OrderingLoginResponseStruct> {
        return try {
            RetroFitInstance.api.getSessionKey(loginRequest)
        } catch (e: Exception) {
            // Handle network error,e.g., log it and throw a custom exception
            Log.e("AppRepository", "Network error: ${e.message}", e)
            throw NetworkErrorException("Failed to connect to the server")
        }
    }

    suspend fun userLogout(logoutRequest: LogoutRequest) =
        RetroFitInstance.logout.logoutRequest(logoutRequest)

    suspend fun getOrderInfo(sessionKey: OrderingRequest): ApiResponse<OrderInfo> {
        return try {
            val response = RetroFitInstance.api2.getOrderInfo(sessionKey)
            if (response.isSuccessful) {
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(response.message(), null)
            }
        } catch (e: Exception) {
            //ApiResponse.UnknownError(e.message ?: "An unknown error occurred")
            Log.e("AppRepository", "Network error: ${e.message}", e)
            ApiResponse.NetworkError(e.message ?: "Failed to connect to the server")
        }
    }

    suspend fun sendOrderEvent(orderEvent: OrderEvent) =
        RetroFitInstance.orderEvent.sendOrderEvent(orderEvent)
}
