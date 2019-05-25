package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.EventItem

/**
 * @author sardor
 */
interface EventsView : BaseView {
    //--Здесь рабочие методы

    fun setEventListData(data: List<EventItem>)
    /*fun setEventFilteredData(data: List<EventItem>)*/

    fun init(size: Int)
}