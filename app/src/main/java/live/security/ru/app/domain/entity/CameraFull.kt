package ru.security.live.domain.entity

import java.io.Serializable

/**
 * @author sardor
 */
data class CameraFull(
        val name: String = "",
        val model: String? = "",
        val location: String = "",
        val id: String = "",
        val url: String? = ""
) : Serializable {
    var archiveAvailable = false
    var archive: ArchiveItem? = null
    var eventArchives: List<ArchiveItem> = ArrayList()


    override fun toString(): String {
        return "id:$id,name:$name"
    }
}