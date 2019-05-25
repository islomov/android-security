package ru.security.live.data.net.entity

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
//Инфа о камерах, которая передаётся с десктопом
data class CamerasItemPOJO(

        @field:SerializedName("savePtz")
        val savePtz: Boolean = false,

        @field:SerializedName("id")
        val id: String = "",

        @field:SerializedName("position")
        val position: Int = -1,

        @field:SerializedName("cameraPositionId")
        val cameraPositionId: Any? = null
)