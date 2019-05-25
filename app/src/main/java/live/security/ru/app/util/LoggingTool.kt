package ru.security.live.util

import android.util.Log
/**
 * @author sardor
 */
class LoggingTool {
    companion object {
        fun log(message: String?) {
            Log.d("appLogTag", message)
        }
    }
}