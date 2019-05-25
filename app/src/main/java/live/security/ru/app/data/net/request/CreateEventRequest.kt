package ru.security.live.data.net.request
/**
 * @author sardor
 */
data class CreateEventRequest(
        val info: String = "",
        val description: String = "",
        val locationId: String = "",
        val deviceId: String = "",
        val typeId: String = "",
        val fileIds: ArrayList<String> = ArrayList()
)