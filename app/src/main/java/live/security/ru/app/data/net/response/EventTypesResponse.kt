package ru.security.live.data.net.response

import ru.security.live.domain.entity.EventTypeItem
import ru.security.live.domain.entity.FilterEventTypeItem
/**
 * @author sardor
 */
data class EventTypesResponse(
        val itemSet: ItemSet = ItemSet()
) {
    data class ItemSet(
            val totalItems: Int = 0,
            val items: List<Item> = emptyList()
    )

    data class Item(
            val id: String = "",
            val name: String = ""
    ) {
        fun toEventTypeItem(): EventTypeItem = EventTypeItem(id, name)
        fun toFilterEventTypeItem(): FilterEventTypeItem = FilterEventTypeItem(id, name, false)
    }
}