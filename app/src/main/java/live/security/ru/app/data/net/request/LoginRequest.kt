package ru.security.live.data.net.request

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class LoginRequest(
        @field:SerializedName("login")
        val login: String? = "Unknown",

        @field:SerializedName("password")
        val password: String? = null,

        @field:SerializedName("accountType")
        val accountType: String? = null
)