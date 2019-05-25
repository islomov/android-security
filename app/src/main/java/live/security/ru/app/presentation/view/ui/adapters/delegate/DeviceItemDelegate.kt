package ru.security.live.presentation.views.ui.adapters.delegate

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_delegate_device.view.*
import ru.security.live.R
import ru.security.live.domain.entity.DeviceItem
import ru.security.live.presentation.view.ui.adapters.with_delegate.BaseDelegateAdapter
/**
 * @author sardor
 */
class DeviceItemDelegate(val onClick: (DeviceItem) -> Unit) : BaseDelegateAdapter<DeviceItem>() {

    override val itemLayoutId: Int get() = R.layout.item_delegate_device

    override fun View.onItemInflated(model: DeviceItem, context: Context) {

        tvName.text = model.name

        itemLayout.setOnClickListener { onClick(model) }
    }

    override fun getViewType(): Int {
        TODO("not implemented")
    }
}