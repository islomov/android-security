package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class LoginResponse(

        @field:SerializedName("tokenId")
        val tokenId: String? = null,

        @field:SerializedName("validUntil")
        val validUntil: String? = null,

        @field:SerializedName("userId")
        val userId: String? = null
) : ErrorResponse()