package ru.security.live.data.net.entity

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class DevicePOJO(

        @field:SerializedName("id")
        val id: String = "",

        @field:SerializedName("name")
        val name: String = ""

)