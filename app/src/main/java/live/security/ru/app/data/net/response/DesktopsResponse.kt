package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
import ru.security.live.data.net.entity.DesktopItemPOJO


/**
 * @author sardor
 */
data class DesktopsResponse(

        @field:SerializedName("itemSet")
        val itemSet: ItemSet = ItemSet()

) : BaseResponse() {
    data class ItemSet(
            @field:SerializedName("totalItems")
            val totalItems: Int = 0,

            @field:SerializedName("items")
            val items: List<DesktopItemPOJO> = emptyList()
    )
}