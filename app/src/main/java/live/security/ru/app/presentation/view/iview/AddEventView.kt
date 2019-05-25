package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.EventTypeItem
/**
 * @author sardor
 */
interface AddEventView : BaseView {
    fun init(list: List<EventTypeItem>)
    fun onEventCreated()
    fun addFileId(hash: Int, id: String)
    fun toggleCreateButton(enable: Boolean)
    fun getLocation()
}