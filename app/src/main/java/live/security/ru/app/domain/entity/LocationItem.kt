package ru.security.live.domain.entity
/**
 * @author sardor
 */
data class LocationItem(
        val name: String,
        val nested: ArrayList<EventLocationItem>

)