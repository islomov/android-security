package ru.security.live.domain.repository

import io.reactivex.Single
import ru.security.live.domain.entity.*
import java.io.File

/**
 * @author sardor
 */
interface IEventsRepository {
    //--Здесь рабочие методы


    fun eventListData(filter: FilterCommon): Single<List<EventItem>>
    fun eventsInitData(filter: FilterCommon): Single<Pair<Int, List<EventItem>>>
    /*fun eventFilteredListData(): Single<List<EventItem>>*/
    fun eventTypes(): Single<List<EventTypeItem>>

    fun createEvent(createEventData: CreateEventData): Single<String>
    fun eventLocationList(): Single<List<EventLocationItem>>
    fun eventChildLocationList(parentId: String): Single<List<EventLocationItem>>

    fun getEvent(id: String): Single<EventDetailed>
    fun getEventSources(): Single<List<FilterEventTypeItem>>
    fun getEventTypes(filter: List<String>): Single<List<FilterEventTypeItem>>
    fun getUserEvent(event: EventDetailed): Single<EventDetailed>
    fun searchDevice(name: String): Single<List<DeviceItem>>

    fun uploadFile(file: File): Single<String>
}