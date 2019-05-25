package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class CameraResponse(

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("hasPtz")
        val hasPtz: Boolean? = null,

        @field:SerializedName("model")
        val model: String? = null,

        @field:SerializedName("location")
        val location: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("broadcastings")
        val broadcastings: List<BroadcastingsItem?>? = null
) {
    data class BroadcastingsItem(

            @field:SerializedName("name")
            val name: String? = null,

            @field:SerializedName("available")
            val available: Boolean? = null,

            @field:SerializedName("weight")
            val weight: Int? = null,

            @field:SerializedName("id")
            val id: String? = null,

            @field:SerializedName("url")
            val url: String? = null
    )
}