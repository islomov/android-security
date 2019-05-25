package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
data class PointsResponse(
        @field:SerializedName("itemSet")
        val itemSet: ItemSet? = null
) {
    data class ItemSet(

            @field:SerializedName("totalItems")
            val totalItems: Int? = null,

            @field:SerializedName("items")
            val items: List<ItemsItem?>? = null
    )

    data class ItemsItem(

            @field:SerializedName("availability")
            val availability: String? = null,

            @field:SerializedName("type")
            val type: String? = null,

            @field:SerializedName("typeCode")
            val typeCode: String? = null,

            @field:SerializedName("surveillanceScene")
            val surveillanceScene: String? = null,

            @field:SerializedName("locationId")
            val locationId: String? = null,

            @field:SerializedName("name")
            val name: String? = null,

            @field:SerializedName("incidents")
            val incidents: List<IncidentsItem?>? = null,

            @field:SerializedName("planId")
            val planId: String? = null,

            @field:SerializedName("id")
            val id: String? = null,

            @field:SerializedName("state")
            val state: String? = null,

            @field:SerializedName("geoCoordinates")
            val geoCoordinates: GeoCoordinates? = null,

            @field:SerializedName("placementDescription")
            val placementDescription: String? = null,

            @field:SerializedName("events")
            val events: List<EventsItem>? = null
    )

    data class GeoCoordinates(

            @field:SerializedName("latitude")
            val latitude: Double? = null,

            @field:SerializedName("longitude")
            val longitude: Double? = null
    )

    data class IncidentsItem(

            @field:SerializedName("count")
            val count: Int? = null,

            @field:SerializedName("priority")
            val priority: Int? = null
    )

    data class EventsItem(

            @field:SerializedName("severity")
            val severity: String? = null,

            @field:SerializedName("count")
            val count: Int? = null
    )
}