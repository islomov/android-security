package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.DesktopsItem

/**
 * @author sardor
 */
interface DesktopsView : BaseView {
    //--Здесь рабочие методы

    fun setDesktopsListData(data: List<DesktopsItem>)

}