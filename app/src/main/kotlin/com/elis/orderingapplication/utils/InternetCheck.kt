package com.elis.orderingapplication.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


object InternetCheck {
    private var job: Job? = null
    private var isConnected: Boolean = false

    fun startMonitoring(context: Context, intervalMillis: Long = 500, onStatusChanged: (Boolean) -> Unit) {
        job = CoroutineScope(Dispatchers.IO).launch {
            while (isActive) {
                val isInternetAvailable = isInternetAvailable()
                if (isInternetAvailable != isConnected) {
                    isConnected = isInternetAvailable
                    withContext(Dispatchers.Main) {
                        onStatusChanged(isConnected)
                    }
                }
                delay(intervalMillis)
            }
        }
    }

    fun stopMonitoring() {
        job?.cancel()
    }

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
            responseCode == 204 // Return true if the response code is 204 (OK)
        } catch (e: IOException) {
            Log.e("Internet Connection", "Error checking internet connection", e)
            false // Return false if an IOException occurs
        }
    }
}

