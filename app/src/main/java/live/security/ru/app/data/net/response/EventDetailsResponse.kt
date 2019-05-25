package ru.security.live.data.net.response

import com.google.gson.Gson
/**
 * @author sardor
 */
data class EventDetailsResponse(
        val event: Event,
        val eventType: EventType,
        val device: Device,
        val surveillanceObjects: SurveillanceObjects,
        val plan: Plan,
        val incident: Any // null
) {
    data class Event(
            val total: Int, // 2
            val totalAmount: Int, // 0
            val adt: Int, // 2
            val adtAmount: Int, // 0
            val body: String, // *** REPORT ***Jul.24,2018, 14:33  Station:1                    count Mode                    --------------------------------TOTAL       2          ---------------------------------ADT         2          ---------------------------------
            val currency: String, // -
            val details: String?, // null
            val id: String, // c1991f8e-9aa9-49cc-a674-70459975b4ea
            val eventDate: String, // 2018-07-24T11:33:08.336309Z
            val registrationDate: String, // 2018-07-24T11:33:00Z
            val isRead: Boolean, // false
            val rewriteTypeKey: String?, // money_bill_counter-event-countval sceneImageUri: String, // http://10.178.3.12:3000/files/af1d778f-46cd-4c1f-83c7-e46225645a52
            val sceneImageUri: String?,
            val vehicleImageUri: String, // http://10.178.3.12:3000/files/b185cf15-750d-434b-91e4-9baa0489fb3c
            val plateImageUri: String, // http://10.178.3.12:3000/files/fbb9b3c8-cca0-481e-95c4-0e8c8ea1d5e6
            val plateNumber: String, // A490EK777
            val speed: Double, // 0.428240746
            val direction: String, // Outgoing
            val imageUri: String? // http://10.178.3.12:3000/files/fbb9b3c8-cca0-481e-95c4-0e8c8ea1d5e6
    ) {

        fun getDetails(): Array<Detail> {
            val gson = Gson()
            return gson.fromJson(details, Array<Detail>::class.java)
        }

        data class Detail(
                val DenominationName: String, // R10
                val Unit: Int, // 0
                val CurrencyName: String, // R
                val Amount: Int, // 0
                val Denomination: Int // 10
        )
    }

    data class Device(
            val id: String, // 3a418cfd-462d-451d-9009-b4fbf811bdf7
            val name: String, // ХО moneyBillCounter_Test
            val type: String, // sensor
            val locations: List<String>,
            val coordinates: Coordinates,
            val placementType: Int // 0
    ) {
        data class Coordinates(
                val latitude: Double, // 0.0
                val longitude: Double // 0.0
        )
    }

    data class EventType(
            val id: String, // 4f4bdb37-0210-4fc9-b55d-b50741aada94
            val name: String, // Простой пересчет
            val key: String?, // money_bill_counter-event-count
            val severity: String, // High
            val eventTypeGroups: String // Счетно-сортировочные машины
    )

    data class SurveillanceObjects(
            val totalItems: Int, // 2
            val items: List<Item>
    ) {
        data class Item(
                val id: String, // 7387dbe4-1d1f-4c80-8982-1f2b2a6dfca9
                val name: String, // Счётная машинка
                val sceneDescription: String, // Счётная машинка
                val createdTimestamp: String, // 2017-05-26T13:02:38.009759Z
                val createdBy: String, // 00000000-0000-0000-0000-000000000009
                val parentLocationId: String, // bb779124-2892-43c2-bf06-e0cc94ac1a30
                val monitoringDevices: List<MonitoringDevice>
        ) {
            data class MonitoringDevice(
                    val id: String, // 3a418cfd-462d-451d-9009-b4fbf811bdf7
                    val name: String, // ХО moneyBillCounter_Test
                    val typeCode: String // sensor
            )
        }
    }

    data class Plan(
            val tileUriTemplate: String? // null
    )
}