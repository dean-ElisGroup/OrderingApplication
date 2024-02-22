package com.elis.orderingapplication.interfaces

import com.elis.orderingapplication.model.LoginRequest
import com.elis.orderingapplication.model.LoginResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface UserLogin {
    @Headers("Content-Type: application/json")
    @POST("Login")
    fun getSessionKey(@Body loginRequest: LoginRequest): Call<List<LoginResponse>>

    companion object {
        private var retrofitService: UserLogin? = null

        fun getInstance(): UserLogin? {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://serviceonlinetest.elisonline.co.uk/SolMasterTraining5/ordering.wso")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(UserLogin::class.java)
            }
            return retrofitService!!
        }
    }
}