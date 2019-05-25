package ru.security.live.data.net.response

import ru.security.live.data.net.entity.Broadcasting

/**
 * @author sardor
 */
data class ArchiveRecordResponse(
        val broadcastings: List<Broadcasting> = emptyList()
)