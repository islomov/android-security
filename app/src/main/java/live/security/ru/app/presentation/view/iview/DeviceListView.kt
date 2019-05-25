package ru.security.live.presentation.view.iview

import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType

/**
 * @author sardor
 */
interface DeviceListView : BaseView {
    //--Здесь рабочие методы

    fun setDeviceListData(data: List<DelegateViewType>)

    fun openNext(data: List<DelegateViewType>)

    fun search(query: String?)

}