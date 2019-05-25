package ru.security.live.data.net.request

import ru.security.live.domain.entity.FilterCommon
import ru.security.live.util.LoggingTool
/**
 * @author sardor
 */
data class EventsRequest(
        val filter: Filter = Filter(),
        val range: Range = Range()) {
    data class Filter(
            val eventTypeGroup: List<Any> = emptyList(),
            val severity: List<Int> = listOf(0, 1, 2),
            val limit: Int = 5000,
            val location: List<Any?> = emptyList(),
            val eventType: List<Any?> = emptyList(),
            val unreadOnly: Boolean = true,
            val eventDate: EventDate? = null)

    data class Range(
            val take: Int = 20,
            val skip: Int = 0
    )

    data class EventDate(
            val from: String = "",
            val to: String = "")

    companion object {
        fun fromFilter(filter: FilterCommon): EventsRequest {
            LoggingTool.log("From ${filter.from} to ${filter.to}")
            // Base filter request should not have to contain date, if there is date in request then we have different result from dashboard
            val eventDate = if (filter.from.contains("00:00:00.000Z") &&
                    filter.to.contains("00:00:00.000Z")) {
                null
            } else {
                EventDate(filter.from, filter.to)
            }
            return EventsRequest(
                    Filter(filter.eventTypeGroup, filter.severity, filter.limit, filter.location,
                            filter.eventType, filter.unreadOnly,
                            eventDate),
                    EventsRequest.Range(filter.take, filter.skip)
            )
        }
    }
}