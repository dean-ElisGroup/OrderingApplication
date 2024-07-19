package com.elis.orderingapplication.utils

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
object InternetCheck {

    suspend fun isInternetAvailable(): Boolean {
        return try {
            val url = URL("http://connectivitycheck.gstatic.com/generate_204")
            val urlConnection: HttpURLConnection =
                withContext(Dispatchers.IO) {
                    url.openConnection() as HttpURLConnection
                }
            urlConnection.setRequestProperty("User-Agent", "Android")
            urlConnection.setRequestProperty("Connection", "close")
            urlConnection.connectTimeout = 1500
            withContext(Dispatchers.IO) {
                urlConnection.connect()
            }
            val responseCode = urlConnection.responseCode
            urlConnection.disconnect()
            responseCode == 204 // Return true if the response code is 200 (OK)
        } catch (e: IOException) {
            Log.e("Internet Connection", "Error checking internet connection", e)
            false // Return false if an IOException occurs
        }
    }
}
