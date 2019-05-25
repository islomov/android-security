package ru.security.live.domain.entity

/**
 * @author sardor
 */
data class EventItem(
        val id: String,
        val name: String,
        val status: String,
        val time: String,
        val importance: String
)