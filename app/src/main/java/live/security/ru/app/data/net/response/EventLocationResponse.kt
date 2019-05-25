package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
import ru.security.live.domain.entity.EventLocationItem
/**
 * @author sardor
 */
data class EventLocationResponse(

        @field:SerializedName("items")
        val items: List<ItemsItem> = emptyList()
) {
    data class ItemsItem(

            @field:SerializedName("id")
            val id: String? = "",

            @field:SerializedName("hasChildren")
            val hasChildren: Boolean? = false,

            @field:SerializedName("name")
            val name: String? = "",

            @field:SerializedName("type")
            val type: String? = "",

            @field:SerializedName("parentId")
            val parentId: String? = ""
    ) {
        fun toEventLocationItem(): EventLocationItem = EventLocationItem(id ?: "",
                hasChildren ?: false, name ?: "", type ?: "", parentId ?: "")
    }
}