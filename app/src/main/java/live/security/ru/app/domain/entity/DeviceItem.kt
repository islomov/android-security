package ru.security.live.domain.entity

import ru.security.live.presentation.view.ui.adapters.with_delegate.DEVICE_ITEM
import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType
/**
 * @author sardor
 */
data class DeviceItem(
        val id: String,
        val name: String,
        val type: String

) : DelegateViewType {
    override val viewType: Int get() = DEVICE_ITEM
} // Потом нужно будет заменить на Device