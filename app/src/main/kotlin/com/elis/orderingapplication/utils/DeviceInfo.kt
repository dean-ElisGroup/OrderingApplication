package com.elis.orderingapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import com.elis.orderingapplication.BuildConfig

class DeviceInfo(private val context: Context) {

    private fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }
    }

    @SuppressLint("HardwareIds")
    private fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    private fun getAppVersion(): String {
        val appVersion = BuildConfig.VERSION_NAME
        return appVersion
    }

    private fun getApplicationName(): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
    }

    fun getDeviceInfo(): String {
        return "Device Name: ${getDeviceName()}\n" +
                "Device ID: ${getDeviceId()}\n" +
                "Application Name: ${getApplicationName()}\n" +
                "Application Version: ${getAppVersion()}"
    }

    private fun capitalize(str: String): String {
        if (str.isNotEmpty()) {
            val firstChar = str[0].uppercaseChar()
            return if (str.length > 1) firstChar + str.substring(1) else firstChar.toString()
        }
        return str
    }
}
