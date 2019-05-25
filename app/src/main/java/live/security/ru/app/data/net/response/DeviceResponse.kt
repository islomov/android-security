package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
import ru.security.live.data.net.entity.DesktopItemPOJO
/**
 * @author sardor
 */
data class DeviceResponse(

        @field:SerializedName("items")
        val items: List<ItemsItem> = emptyList()
) {
    data class ItemsItem(

            @field:SerializedName("desktops")
            val desktops: List<DesktopItemPOJO> = emptyList(),

            @field:SerializedName("hasChildren")
            val hasChildren: Boolean = false,

            @field:SerializedName("name")
            val name: String = "",

            @field:SerializedName("id")
            val id: String = "",

            @field:SerializedName("type")
            val type: String = "",

            @field:SerializedName("parentId")
            val parentId: String = ""
    )
}