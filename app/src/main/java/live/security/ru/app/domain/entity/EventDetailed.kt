package ru.security.live.domain.entity

import ru.security.live.data.net.response.PointsResponse
import ru.security.live.domain.entity.enums.EventType
import ru.security.live.util.*
/**
 * @author sardor
 */
class EventDetailed(
        val id: String,
        val source: String,
        val location: String?,
        val time: String,
        val message: String,
        val type: EventType) {

    var userEventData: UserEventData? = null
    var mixedEventData: MixedEventData? = null
    var grnData: GRNData? = null
    var faceCapturedData: FaceCapturedData? = null
    var camera: Camera? = null
    var device: Device? = null

    var body: String? = null

    fun isCamera(): Boolean {
        return camera != null
    }

    fun getArchiveStart() = getEventArchiveStartTime(time)

    fun getArchiveEnd() = getEventArchiveEndTime(time)

    fun getArchiveStartUTC() = getEventArchiveStart(time)

    fun getArchiveEndUTC() = getEventArchiveEnd(time)

    fun getFormattedTime() = reformatDate(time)

    data class UserEventData(
            val message: String,
            val images: List<String>?
    )

    data class MixedEventData(
            val body: String,
            val denominations: List<String>,
            val amounts: List<String>,
            val total: List<String>
    ) {
        fun getDenominationText(): String {
            val builder = StringBuilder()
            denominations.forEach {
                builder.append(it)
                builder.append("\n")
            }
            return builder.toString()
        }

        fun getAmountText(): String {
            val builder = StringBuilder()
            amounts.forEach {
                builder.append(it)
                builder.append("\n")
            }
            return builder.toString()
        }

        fun getTotalText(): String {
            val builder = StringBuilder()
            total.forEach {
                builder.append(it)
                builder.append("\n")
            }
            return builder.toString()
        }
    }

    data class GRNData(
            val plateNumber: String,
            val speed: Double,
            val direction: String,
            val images: List<String?>
    )

    data class FaceCapturedData(
            val name: String,
            val image: String?
    ) {
        fun getImageList(): List<String> {
            return listOf(image).filter { !it.isNullOrEmpty() }
                    .map { it!! }
        }
    }

    data class Camera(
            val cameraId: String,
            val name: String
    )

    data class Device(
            val id: String,
            val events: List<PointsResponse.EventsItem>,
            val name: String,
            val location: String,
            val deviceType: String,
            val placementType: Int,
            val lat: Double = 0.0,
            val lon: Double = 0.0,
            var tileTemplate: String? = null
    ) {
        fun isMap(): Boolean {
            return lat != 0.0 && lon != 0.0
        }


    }
}