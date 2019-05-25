package ru.security.live.data.net.entity

import com.google.gson.annotations.SerializedName
import ru.security.live.domain.entity.DesktopsCamItem
import ru.security.live.domain.entity.DesktopsItem
/**
 * @author sardor
 */
data class DesktopItemPOJO(

        @field:SerializedName("cameras")
        val cameras: ArrayList<CamerasItemPOJO> = ArrayList<CamerasItemPOJO>(),

        @field:SerializedName("videoLayoutCode")
        val videoLayoutCode: String = "",

        @field:SerializedName("locationId")
        val locationId: Any? = null,

        @field:SerializedName("name")
        val name: String = "",

        @field:SerializedName("id")
        val id: String = ""
) {
    fun toDesktopsItem(): DesktopsItem = DesktopsCamItem(id, name, ArrayList(cameras.map { it.id }))
}