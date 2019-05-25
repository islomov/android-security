package ru.security.live.data.net.response

import com.google.gson.annotations.SerializedName
/**
 * @author sardor
 */
class BuildingPlanResponse(
        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("tileUriTemplate")
        val tileUriTemplate: String? = null,

        @field:SerializedName("status")
        val status: String? = null,

        @field:SerializedName("points")
        val points: List<Points>? = null
) {

    data class Points(
            val placementDescription: String?, // null
            val surveillanceScene: String?, // null
            val typeCode: String?, // camera
            val state: String, // InUse
            val availability: String?, // null
            val locationId: String?, // 75a189c6-5ea7-47ba-b976-15c8279f6a92
            val events: List<PointsResponse.EventsItem>?, // null
            val incidents: List<Incident>?,
            val objectType: String, // device
            val id: String, // 1007fa8b-84f2-411e-a1ea-68bc0a2e2ccd
            val name: String, // Стройка 1
            val x: Double?, // 25.4829521
            val y: Double? // -5.625
    ) {
        data class Incident(
                val priority: Int, // 2
                val count: Int // 9
        )
    }
}