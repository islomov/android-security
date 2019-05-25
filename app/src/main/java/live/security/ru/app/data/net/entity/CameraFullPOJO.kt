package ru.security.live.data.net.entity

import com.google.gson.annotations.SerializedName
import ru.security.live.domain.entity.CameraFull

/**
 * @author sardor
 */
data class CameraFullPOJO(

        @field:SerializedName("name")
        val name: String = "",

        @field:SerializedName("hasPtz")
        val hasPtz: Boolean = false,

        @field:SerializedName("model")
        val model: String? = "",

        @field:SerializedName("location")
        val location: String = "",

        @field:SerializedName("id")
        val id: String = "",

        @field:SerializedName("broadcastings")
        val broadcastings: ArrayList<Broadcasting>? = ArrayList()
) {
    fun toCameraFull(): CameraFull = CameraFull(name, model, location, id,
            if (broadcastings == null || broadcastings.isEmpty() || broadcastings.first().url.isNullOrEmpty())
                null else broadcastings.first().url)
}