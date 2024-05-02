package com.elis.orderingapplication.retrofit

import com.elis.orderingapplication.interfaces.OrderInfo
import com.elis.orderingapplication.interfaces.UserLogin
import com.elis.orderingapplication.interfaces.OrderEvent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.solstockorder.dev.utils.ApiUrls.Companion.BASE_URL
import java.time.Duration
import java.util.concurrent.TimeUnit

class RetroFitInstance {
    companion object {
        private val retrofit: Retrofit by lazy {
            val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(Duration.ZERO)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

        }
        val api by lazy {
            retrofit.create(UserLogin::class.java)
        }
        val api2 by lazy {
            retrofit.create(OrderInfo::class.java)
        }
        val orderEvent by lazy {
            retrofit.create(OrderEvent::class.java)
        }
    }
}