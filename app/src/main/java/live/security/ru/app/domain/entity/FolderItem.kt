package ru.security.live.domain.entity

import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType
import ru.security.live.presentation.view.ui.adapters.with_delegate.FOLDER_ITEM
/**
 * @author sardor
 */
data class FolderItem(
        val name: String,
        val nested: ArrayList<DelegateViewType>

) : DelegateViewType {
    override val viewType: Int get() = FOLDER_ITEM
}