package ru.security.live.domain.entity

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.security.live.data.net.response.PointsResponse
import ru.security.live.util.StatusMap
import java.io.Serializable
/**
 * @author sardor
 */
open class Point(open var info: Info) : Serializable {
    fun mapToMarker(mapView: MapView): Marker {
        val m = Marker(mapView)
        val position = GeoPoint(info.lat, info.long)
        m.position = position
        return m
    }
}

class Building(
        override var info: Info,
        val description: String?,
        val planId: String?) : Point(info)

class Device(
        override var info: Info,
        val scene: String?,
        val placement: String?,
        val incidents1: Int?,
        val incidents2: Int?,
        val state: String?,
        val broadcastAvailable: Boolean
) : Point(info) {
    fun getStatus(): String {
        println(state)
        println(StatusMap.map.keys)
        return StatusMap.map[state ?: ""]!!
    }
}

data class Info(
        val type: String?,
        val id: String,
        val events: List<PointsResponse.EventsItem>,
        val title: String,
        val lat: Double,
        val long: Double,
        val locationId: String?
)

class Cluster(
        override var info: Info,
        val list: List<Point>) : Point(info)