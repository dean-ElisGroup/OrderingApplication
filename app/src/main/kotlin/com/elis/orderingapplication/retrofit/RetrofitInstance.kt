package com.elis.orderingapplication.retrofit

import com.elis.orderingapplication.interfaces.UserLogin
//import com.elis.orderingapplication.utils.ApiUrls.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.solstockorder.dev.utils.ApiUrls.Companion.BASE_URL

class RetroFitInstance {
    companion object {
        val retrofit: Retrofit by lazy {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client).build()

        }
        val api by lazy {
            retrofit.create(UserLogin::class.java)
        }
    }
}