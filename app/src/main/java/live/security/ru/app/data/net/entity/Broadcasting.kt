package ru.security.live.data.net.entity

/**
 * @author sardor
 */
data class Broadcasting(
        val name: String = "",
        val available: Boolean = false,
        val weight: Int = -1,
        val id: String = "",
        val url: String? = ""
)