package ru.security.live.domain.entity

import android.os.Parcel
import android.os.Parcelable
import ru.security.live.util.allChecked
import ru.security.live.util.noneChecked
/**
 * @author sardor
 */
data class EventLocationItem(
        val id: String = "",
        val hasChildren: Boolean = false,
        val name: String = "",
        val type: String = "",
        val parentId: String = ""
) : Parcelable {


    public var status: Int = noneChecked
    public var isParent = false

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
        status = parcel.readInt()
        isParent = parcel.readByte() != 0.toByte()
    }

    fun toggle() {
        if (status == allChecked)
            status = noneChecked
        else
            status = allChecked
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeByte(if (hasChildren) 1 else 0)
        parcel.writeString(name)
        parcel.writeString(type)
        parcel.writeString(parentId)
        parcel.writeInt(status)
        parcel.writeByte(if (isParent) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<EventLocationItem> {
        override fun createFromParcel(parcel: Parcel): EventLocationItem {
            return EventLocationItem(parcel)
        }

        override fun newArray(size: Int): Array<EventLocationItem?> {
            return arrayOfNulls(size)
        }
    }
}