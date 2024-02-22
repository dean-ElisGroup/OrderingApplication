package com.elis.orderingapplication.repositories

import com.elis.orderingapplication.interfaces.UserLogin
import com.elis.orderingapplication.model.LoginRequest

class UserLoginRepository constructor(private val retrofitService: UserLogin) {
    fun getUserLogin() =
        retrofitService.getSessionKey(LoginRequest("SOUHMEADOPD1", "Tuesday1"))
}