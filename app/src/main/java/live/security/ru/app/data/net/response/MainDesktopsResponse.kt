package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class MainDesktopsResponse(
        @field:SerializedName("itemSet")
        val itemSet: ItemSet? = null
) {
    data class ItemSet(

            @field:SerializedName("totalItems")
            val totalItems: Int? = null,

            @field:SerializedName("items")
            val items: List<ItemsItem?>? = null
    )

    data class ItemsItem(

            @field:SerializedName("cameras")
            val cameras: List<CamerasItem?>? = null,

            @field:SerializedName("videoLayoutCode")
            val videoLayoutCode: String? = null,

            @field:SerializedName("locationId")
            val locationId: Any? = null,

            @field:SerializedName("name")
            val name: String? = null,

            @field:SerializedName("id")
            val id: String? = null
    )

    data class CamerasItem(

            @field:SerializedName("savePtz")
            val savePtz: Boolean? = null,

            @field:SerializedName("id")
            val id: String? = null,

            @field:SerializedName("position")
            val position: Int? = null,

            @field:SerializedName("cameraPositionId")
            val cameraPositionId: Any? = null
    )
}