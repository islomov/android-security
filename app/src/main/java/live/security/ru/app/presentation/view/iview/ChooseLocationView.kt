package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.EventLocationItem
/**
 * @author sardor
 */
interface ChooseLocationView : BaseView {
    fun setDeviceListData(data: List<EventLocationItem>)
    fun openNext(data: List<EventLocationItem>)
}