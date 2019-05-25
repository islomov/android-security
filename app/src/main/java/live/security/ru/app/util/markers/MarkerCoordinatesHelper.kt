package ru.security.live.util.markers

import org.osmdroid.util.BoundingBox
import org.osmdroid.views.overlay.Marker
import ru.security.live.util.LoggingTool

/**
 * @author sardor
 */
class MarkerCoordinatesHelper {
    companion object {
        fun getMinLat(items: ArrayList<Marker>): Double {
            val r = items.asSequence().map { it.position.latitude }.min()!!
            LoggingTool.log("getMinLat $r")
            return r
        }

        fun getMinLon(items: ArrayList<Marker>): Double {
            val r = items.asSequence().map { it.position.longitude }.min()!!
            LoggingTool.log("getMinLon $r")
            return r
        }

        fun getMaxLat(items: ArrayList<Marker>): Double {
            val r = items.asSequence().map { it.position.latitude }.max()!!
            LoggingTool.log("getMaxLat $r")
            return r
        }

        fun getMaxLon(items: ArrayList<Marker>): Double {
            val r = items.asSequence().map { it.position.longitude }.max()!!
            LoggingTool.log("getMaxLon $r")
            return r
        }

        fun getBoundingBox(items: ArrayList<Marker>): BoundingBox {
            return BoundingBox(getMaxLat(items), getMaxLon(items),
                    getMinLat(items), getMinLon(items))
        }

    }
}