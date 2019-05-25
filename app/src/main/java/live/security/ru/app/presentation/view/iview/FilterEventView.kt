package ru.security.live.presentation.view.iview

import ru.security.live.presentation.view.ui.adapters.FilterEventTypeAdapter
import java.util.*
/**
 * @author sardor
 */
interface FilterEventView : BaseView {
    fun showSources(adapter: FilterEventTypeAdapter)
    fun showEvents(adapter: FilterEventTypeAdapter)
    fun showDatePicker(calendar: Calendar, isFrom: Boolean)
    fun showTimePicker(calendar: Calendar, isFrom: Boolean)
    fun onRangeUpdate()
    fun runOnMainThread(action: () -> Unit)
    fun resetFilters()
}