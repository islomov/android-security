package ru.security.live.presentation.view.ui.adapters.delegate

import android.content.Context
import android.view.View
import kotlinx.android.synthetic.main.item_desktops_list.view.*
import ru.security.live.R
import ru.security.live.domain.entity.FolderItem
import ru.security.live.presentation.view.ui.adapters.with_delegate.BaseDelegateAdapter
/**
 * @author sardor
 */
class FolderDelegate(val onClick: (FolderItem) -> Unit) : BaseDelegateAdapter<FolderItem>() {

    override val itemLayoutId: Int get() = R.layout.item_desktops_list

    override fun View.onItemInflated(model: FolderItem, context: Context) {

        tvName.text = model.name

        itemLayout.setOnClickListener { onClick(model) }
    }

    override fun getViewType(): Int {
        TODO("not implemented")
    }
}

