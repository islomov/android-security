package ru.security.live.domain.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
/**
 * @author sardor
 */
@Parcelize
data class Plan(
        val url: String,
        val id: String
) : Parcelable