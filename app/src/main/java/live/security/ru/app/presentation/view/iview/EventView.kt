package ru.security.live.presentation.view.iview

import android.view.View
import android.widget.ImageView
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull
import ru.security.live.domain.entity.EventDetailed
/**
 * @author sardor
 */
interface EventView : BaseView {
    fun showDetails(event: EventDetailed, camera: CameraFull?, archives: List<ArchiveItem>)
    fun handleVisibility(arrow: ImageView, details: Array<View>)
    fun openElement(arrow: ImageView, details: Array<View>)
    fun closeElement(arrow: ImageView, details: Array<View>)
    fun setUpMap(device: EventDetailed.Device)
}