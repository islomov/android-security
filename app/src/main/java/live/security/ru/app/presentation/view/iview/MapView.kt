package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.Plan
import ru.security.live.domain.entity.Point
/**
 * @author sardor
 */
interface MvpMapView : BaseView {
    fun updatePoints(points: Collection<Point>)
    fun setMarkers()
    fun openPlan(plan: Plan)
    fun getLocation()
}