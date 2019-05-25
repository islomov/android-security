package ru.security.live.util

import android.content.Context
import android.net.ConnectivityManager

/**
 * @author sardor
 */
class ConnectivityTool {
    companion object {
        lateinit var applicationContext: Context

        fun isNetworkAvailable(): Boolean {
            val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager
                    .activeNetworkInfo
            return activeNetworkInfo != null
        }
    }
}