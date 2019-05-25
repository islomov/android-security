package ru.security.live.domain.entity

/**
 * @author sardor
 */
data class FilterCommon(
        val severity: List<Int> = listOf(0, 1, 2),
        val take: Int = 20,
        val skip: Int = 0,
        val from: String = "",
        val to: String = "",
        val limit: Int = 5000,
        val unreadOnly: Boolean = true,
        val eventTypeGroup: List<String> = emptyList(),
        val location: List<String?> = emptyList(),
        val eventType: List<String?> = emptyList()
)