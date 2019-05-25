package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.EventLocationItem
/**
 * @author sardor
 */
interface EquipmentsView : BaseView {
    fun showList(array: ArrayList<EventLocationItem>)
    fun openChild(item: EventLocationItem)
    fun resetFilter()
    fun updateFilters()
    fun checkChildren(array: ArrayList<EventLocationItem>)
    fun uncheckChildren(array: ArrayList<EventLocationItem>)
    fun parentsLoad(pair: Pair<EventLocationItem, ArrayList<EventLocationItem>>)
}