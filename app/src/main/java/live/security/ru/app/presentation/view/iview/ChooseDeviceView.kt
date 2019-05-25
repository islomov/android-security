package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.DeviceItem
/**
 * @author sardor
 */
interface ChooseDeviceView : BaseView {
    fun showResult(list: ArrayList<DeviceItem>)
    fun cleanList()
}