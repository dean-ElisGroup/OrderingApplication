package com.elis.orderingapplication.repositories

import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.LogoutRequest
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.pojo2.OrderEvent
import com.elis.orderingapplication.retrofit.RetroFitInstance

class UserLoginRepository {
    suspend fun getUserLogin(loginRequest: LoginRequest) =
        RetroFitInstance.api.getSessionKey(loginRequest)

    suspend fun userLogout(logoutRequest: LogoutRequest) =
        RetroFitInstance.logout.logoutRequest(logoutRequest)

    suspend fun getOrderInfo(sessionKey: OrderingRequest) =
        RetroFitInstance.api2.getOrderInfo(sessionKey)

    suspend fun sendOrderEvent(orderEvent: OrderEvent) =
        RetroFitInstance.orderEvent.sendOrderEvent(orderEvent)
}
