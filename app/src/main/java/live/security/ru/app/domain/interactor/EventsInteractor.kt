package ru.security.live.domain.interactor

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.security.live.domain.entity.*
import ru.security.live.domain.repository.IEventsRepository
import java.io.File


/**
 * @author sardor
 */
class EventsInteractor(private val repository: IEventsRepository) {

    //--Здесь рабочие методы

    fun getEvent(id: String): Single<EventDetailed> {
        return repository.getEvent(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getEventSources(): Single<List<FilterEventTypeItem>> {
        return repository.getEventSources()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getEventTypes(checkedSources: List<FilterEventTypeItem>): Single<List<FilterEventTypeItem>> {
        return repository.getEventTypes(checkedSources.map { it.id })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun eventListData(filter: FilterCommon): Single<List<EventItem>> {
        return repository.eventListData(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun eventsInitData(filter: FilterCommon): Single<Pair<Int, List<EventItem>>> {
        return repository.eventsInitData(filter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun eventTypes(): Single<List<EventTypeItem>> {
        return repository.eventTypes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun createEvent(data: CreateEventData): Single<String> {
        return repository.createEvent(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun locationListStructure(): Single<List<EventLocationItem>> {
        return repository.eventLocationList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun locationMoreListStructure(parentId: String): Single<List<EventLocationItem>> {
        return repository.eventChildLocationList(parentId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun uploadFile(file: File): Single<String> {
        return repository.uploadFile(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun searchDevice(name: String): Single<List<DeviceItem>> {
        return repository.searchDevice(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}