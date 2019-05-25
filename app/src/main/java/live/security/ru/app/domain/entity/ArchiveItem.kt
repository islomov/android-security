package ru.security.live.domain.entity

import ru.security.live.util.DateTool
import ru.security.live.util.getLocalDate
import java.io.Serializable
import java.util.*
/**
 * @author sardor
 */
data class ArchiveItem(
        val from: String = "",
        val to: String = "",
        var url: String = "",
        val weight: Int = 0
) : DesktopsItem("from $from to $to"), Serializable {

    fun getStart() = getLocalDate(from)

    fun getEnd() = getLocalDate(to)


    fun getEndCalendar(): Calendar {
        return DateTool.getCalendar(to, DateTool.fullDateSec)
    }

    fun getDuration(): Int {
        return (getEnd().time - getStart().time).toInt()
    }

    fun getDuration(start: Date): Int {
        return (getEnd().time - start.time).toInt()
    }
}