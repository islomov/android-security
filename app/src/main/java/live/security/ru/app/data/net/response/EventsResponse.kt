package ru.security.live.data.net.response

import ru.security.live.domain.entity.EventItem
import ru.security.live.util.reformatDate
/**
 * @author sardor
 */
data class EventsResponse(
        val itemSet: ItemSet = ItemSet()
) {
    data class ItemSet(
            val totalItems: Int = 0,
            val hasOverTotalItems: Boolean = false,
            val items: List<ItemsItem> = emptyList()
    )

    data class ItemsItem(
            val severity: String = "",
            val registrationDate: String = "",
            val name: String = "",
            val isRead: Boolean = false,
            val locations: List<String> = emptyList(),
            val id: String = "",
            val incidentId: Any? = null,
            val deviceName: String = "",
            val properties: String = "",
            val eventDate: String = "") {

        fun toEventItem(): EventItem = EventItem(id, deviceName, name, reformatDate(eventDate), severity)
    }
}