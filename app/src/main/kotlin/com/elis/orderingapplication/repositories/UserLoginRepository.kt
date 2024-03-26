package com.elis.orderingapplication.repositories

import com.elis.orderingapplication.database.UserLoginDao
import com.elis.orderingapplication.database.UserLoginInsert
import com.elis.orderingapplication.database.UserLogin
import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.OrderingRequest
import com.elis.orderingapplication.retrofit.RetroFitInstance

class UserLoginRepository {
    suspend fun getUserLogin(loginRequest: LoginRequest) =
        RetroFitInstance.api.getSessionKey(loginRequest)

    suspend fun getOrderInfo(sessionKey: OrderingRequest) =
        RetroFitInstance.api2.getOrderInfo(sessionKey)
 }
