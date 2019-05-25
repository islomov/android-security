package ru.security.live.presentation.view.iview

import java.util.*

/**
 * @author sardor
 */
interface ArchiveView : BaseView {
    fun updateLink(media: String)
    fun initPlayer(media: String)
    fun releasePlayer()
    fun showDatePicker(start: Long, end: Long)
    fun showTimePicker(calendar: Calendar)
    fun showVideoLoading()
    fun hideVideoLoading()

    fun setUpTexture()
    fun removeTexture()
    fun addTexture()

    fun createUserEvent()
    fun pausePlayer()
    fun handlePlayerControl(play: Boolean)
    fun enableNavButtons(enable: Boolean)

    fun setUpScale(calendarStart: Calendar)
    fun scheduleAutoScroll()
    fun scrollScaleToDate(timestamp: Long)
}