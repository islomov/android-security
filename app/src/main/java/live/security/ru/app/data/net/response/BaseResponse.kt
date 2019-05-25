package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
open class BaseResponse(
        @field:SerializedName("result")
        val result: String = "",
        @field:SerializedName("message")
        val message: String = ""
)
