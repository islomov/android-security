package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class CameraArchiveResponse(

        @field:SerializedName("broadcastings")
        val broadcastings: List<BroadcastingsItem?>? = null
) {
    data class BroadcastingsItem(

            @field:SerializedName("weigth")
            val weigth: Int? = null,

            @field:SerializedName("name")
            val name: String? = null,

            @field:SerializedName("available")
            val available: Boolean? = null,

            @field:SerializedName("id")
            val id: String? = null,

            @field:SerializedName("url")
            val url: String? = null
    )
}