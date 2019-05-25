package ru.security.live.data.net.entity

import com.google.gson.annotations.SerializedName

/**
 * @author sardor
 */
data class EventBroadCast(
        @field:SerializedName("broadcastings")
        val broadcastings: ArrayList<Broadcasting>? = ArrayList())