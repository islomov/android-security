package ru.security.live.data.net.response

import ru.security.live.data.net.entity.DevicePOJO
/**
 * @author sardor
 */
data class SearchDeviceResponse(
        val itemSet: ItemSet
) {
    data class ItemSet(
            val totalItems: Int, // 1
            val items: List<DevicePOJO>
    )
}