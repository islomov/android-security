package ru.security.live.data.net

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import ru.security.live.data.net.entity.CameraFullPOJO
import ru.security.live.data.net.entity.EventBroadCast
import ru.security.live.data.net.request.CreateEventRequest
import ru.security.live.data.net.request.EventsRequest
import ru.security.live.data.net.response.*
import ru.security.live.domain.entity.ArchiveItem

/**
 * @author sardor
 */
interface PrivateApi {


    @GET("authentication/user")
    fun getUser(): Single<UserResponse>

    @GET("authentication/permissions")
    fun getPermissions(): Single<ArrayList<PermissionsResponse>>

    //VIDEO MONITORING

    @GET("/videomonitoring/desktops")
    fun getDesktopsList(): Single<DesktopsResponse>


    @GET("/videomonitoring/cameras/{id}")
    fun getCamera(@Path("id") id: String): Single<CameraFullPOJO>

    @GET("/videomonitoring/addresses/addressTree")
    fun getRootDeviceList(): Single<DeviceResponse>

    @GET("/videomonitoring/addresses/addressTree")
    fun getChildDeviceList(@Query("filter.parentId") parentId: String): Single<DeviceResponse>

    //ARCHIVE MONITORING

    @GET("/archivemonitoring/archive")
    fun getRecord(@Query("cameraId") id: String,
                  @Query("from") from: String,
                  @Query("to") to: String): Single<ArchiveRecordResponse>


    @GET("/archivemonitoring/archive/list")
    fun getArchiveList(@Query("cameraId") id: String): Single<List<ArchiveItem>>


    //MAP POINTS

    @GET("map/points")
    fun getMapPoints(): Observable<PointsResponse>

    @GET("plans/{id}")
    fun getBuildingPlan(@Path("id") id: String): Observable<BuildingPlanResponse>

    //EVENTS

    @POST("events")
    fun getEvents(@Body eventsRequest: EventsRequest): Single<EventsResponse>

    @GET("events/cameras/{id}")
    fun getCameraForEvent(@Path("id") id: String): Single<CameraFullPOJO>


    @GET("/events/{id}")
    fun getEvent(@Path("id") id: String): Single<EventDetailsResponse>

    @GET("events/userevent/{id}")
    fun getUserEvent(@Path("id") id: String): Single<UserEventResponse>

    @GET("events/lookup/userEventTypeGroup")
    fun getUserEventSources(): Single<EventTypesResponse>

    @GET("events/eventTypeGroups")
    fun getEventSources(): Single<EventTypesResponse>

    @GET("events/eventTypes")
    fun getEventTypes(@Query("filter[eventTypeGroupId]") filters: List<String>): Single<EventTypesResponse>

    @GET("/events/cameras/{id}/archive")
    fun getArchivesForEvent(@Path("id") id: String,
                            @Query("from") from: String,
                            @Query("to") to: String): Single<EventBroadCast>

    @POST("/events/adduserevent")
    fun createEvent(@Body createEventRequest: CreateEventRequest): Single<BaseResponse>

    @GET("events/addresses/addresstree")
    fun getEventLocationList(): Single<EventLocationResponse>

    @GET("/events/lookup/devices")
    fun searchDevices(
            @Query("limit") limit: Int = 10,
            @Query("name") name: String
    ): Single<SearchDeviceResponse>

    @GET("events/addresses/addresstree")
    fun getEventChildLocationList(
            @Query("filter.parentId") parentId: String): Single<EventLocationResponse>

    @Multipart
    @POST("/events/files")
    fun postFile(@Part("file") request: RequestBody,
                 @Part file: MultipartBody.Part): Single<PostFileResponse>
}