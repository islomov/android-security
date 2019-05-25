package ru.security.live.domain.entity

/**
 * @author sardor
 */
data class CreateEventData(
        val info: String,
        val description: String,
        val locationId: String,
        val deviceId: String,
        val typeId: String,
        val fileIds: ArrayList<String>
)