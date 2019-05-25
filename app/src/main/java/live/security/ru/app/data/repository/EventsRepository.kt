package ru.security.live.data.repository

import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import ru.security.live.data.net.ApiHolder
import ru.security.live.data.net.request.CreateEventRequest
import ru.security.live.data.net.request.EventsRequest
import ru.security.live.domain.entity.*
import ru.security.live.domain.entity.enums.EventType
import ru.security.live.domain.repository.IEventsRepository
import ru.security.live.util.*
import java.io.File
/**
 * @author sardor
 */
object EventsRepository : IEventsRepository {

    override fun eventListData(filter: FilterCommon): Single<List<EventItem>> {
        return ApiHolder.privateApi.getEvents(EventsRequest.fromFilter(filter))
                .map { response -> response.itemSet.items.map { it.toEventItem() } }
//                .doOnSuccess { eventFilteredData = it }
    }

    override fun eventsInitData(filter: FilterCommon): Single<Pair<Int, List<EventItem>>> {
        val request = EventsRequest.fromFilter(filter)
        return ApiHolder.privateApi.getEvents(request)
                .map { response ->
                    Pair(response.itemSet.totalItems, response.itemSet.items.map { it.toEventItem() })
                }
    }

    override fun eventTypes(): Single<List<EventTypeItem>> {
        return ApiHolder.privateApi.getUserEventSources()
                .map { response -> response.itemSet.items.map { it.toEventTypeItem() } }
    }

    override fun createEvent(createEventData: CreateEventData): Single<String> {
        return ApiHolder.privateApi.createEvent(CreateEventRequest(createEventData.info, createEventData.description,
                createEventData.locationId, createEventData.deviceId, createEventData.typeId, createEventData.fileIds))
                .map { it.result }
    }

    override fun eventLocationList(): Single<List<EventLocationItem>> {
        return ApiHolder.privateApi.getEventLocationList()
                .map { response ->

                    response.items.map { it.toEventLocationItem() }
                }
    }

    override fun eventChildLocationList(parentId: String): Single<List<EventLocationItem>> {
        return ApiHolder.privateApi.getEventChildLocationList(parentId)
                .map { response ->
                    response.items.map { it.toEventLocationItem() }
                }
    }

    override fun getEvent(id: String): Single<EventDetailed> {
        return ApiHolder.privateApi.getEvent(id)
                .flatMap {
                    val type =
                            if ((!it.event.rewriteTypeKey.isNullOrEmpty() && it.event.rewriteTypeKey != "undefined")
                                    || (!it.eventType.key.isNullOrEmpty() && it.eventType.key != "undefined")) {
                                when (it.event.rewriteTypeKey) {
                                    eventTypeUserCreate -> EventType.UserEvent
                                    eventTypeEventCount -> EventType.EventCount
                                    eventTypeEventMix -> EventType.EventMix
                                    eventTypeEventCounterError -> EventType.EventMix
                                    eventTypeEventSingle -> EventType.EventMix
                                    eventTypeEventOrientation -> EventType.EventMix
                                    eventTypeEventFade -> EventType.EventMix
                                    eventTypeGRNRecognized -> EventType.GRNRecognized
                                    else -> EventType.FaceCaptured
                                }
                            } else {
                                EventType.Default
                            }
                    val event = EventDetailed(it.event.id, it.device.type, it.device.name, it.event.eventDate, it.eventType.name, type)
                    event.device = EventDetailed.Device(it.device.id, mutableListOf(), it.device.name,
                            it.device.locations.toString(), it.device.type, it.device.placementType, it.device.coordinates.latitude,
                            it.device.coordinates.longitude, it.plan.tileUriTemplate)

                    if (it.surveillanceObjects.totalItems > 0) {
                        var camera: EventDetailed.Camera? = null
                        for (item in it.surveillanceObjects.items) {
                            for (device in item.monitoringDevices) {
                                if (device.typeCode == "camera") {
                                    camera = EventDetailed.Camera(device.id, device.name)
                                    break
                                }
                            }
                            if (camera != null)
                                break
                        }
                        event.camera = camera
                    }

                    when (type) {
                        EventType.EventCount -> {
                            event.body = it.event.body
                        }
                        EventType.EventMix -> {
                            if (it.event.details != null) {
                                event.mixedEventData = EventDetailed.MixedEventData(it.event.body,
                                        it.event.getDetails().map { detail -> detail.Denomination.toString() },
                                        it.event.getDetails().map { detail -> detail.Amount.toString() },
                                        it.event.getDetails().map { detail -> (detail.Denomination * detail.Amount).toString() }
                                )
                            } else {
                                event.mixedEventData = EventDetailed.MixedEventData(it.event.body, listOf(), listOf(), listOf())
                            }
                        }
                        EventType.GRNRecognized -> {
                            event.grnData = EventDetailed.GRNData(it.event.plateNumber, it.event.speed, it.event.direction,
                                    listOf(it.event.sceneImageUri, it.event.vehicleImageUri, it.event.plateImageUri))
                        }
                        EventType.FaceCaptured -> {
                            event.faceCapturedData = EventDetailed.FaceCapturedData(it.eventType.name, it.event.imageUri)
                        }
                        else -> {
                        }
                    }

                    if (type == EventType.UserEvent)
                        getUserEvent(event)
                    else
                        Single.just(event)
                }
    }

    override fun getUserEvent(event: EventDetailed): Single<EventDetailed> {
        return ApiHolder.privateApi.getUserEvent(event.id)
                .map { result ->
                    event.userEventData = EventDetailed.UserEventData(result.description,
                            result.images?.map { it.uri })
                    event
                }
    }

    override fun getEventSources(): Single<List<FilterEventTypeItem>> {
        return ApiHolder.privateApi.getEventSources()
                .map { it.itemSet.items.map { it.toFilterEventTypeItem() } }
    }

    override fun getEventTypes(filter: List<String>): Single<List<FilterEventTypeItem>> {
        return ApiHolder.privateApi.getEventTypes(filter)
                .map { it.itemSet.items.map { it.toFilterEventTypeItem() } }
    }

    override fun uploadFile(file: File): Single<String> {
        val requestFile = RequestBody.create(MediaType.parse("image/jpeg"), file)
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        return ApiHolder.privateApi.postFile(requestFile, body)
                .map { it.imageInfo.id }
    }

    override fun searchDevice(name: String): Single<List<DeviceItem>> {
        return ApiHolder.privateApi.searchDevices(name = name)
                .map {
                    it.itemSet.items.map { DeviceItem(it.id, it.name, "") }
                }
    }
}