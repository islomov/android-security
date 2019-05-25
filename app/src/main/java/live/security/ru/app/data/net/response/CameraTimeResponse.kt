package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class CameraTimeResponse(

        @field:SerializedName("from")
        val from: String? = null,

        @field:SerializedName("to")
        val to: String? = null
)