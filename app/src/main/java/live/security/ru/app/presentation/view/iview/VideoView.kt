package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.CameraFull
/**
 * @author sardor
 */
interface VideoView : BaseView {
    fun setUpDesktop()
    fun setVideos(list: List<CameraFull>)
    fun refreshVideoList(list: List<CameraFull>)
    fun onArchiveChecked()
    fun setCurrentVideo(cameraFull: CameraFull)
    fun initPlayer(media: String)
    fun releasePlayer()
    fun showVideoLoading()
    fun hideVideoLoading()
    fun createUserEvent()
    fun enableNavButtons(enable: Boolean)
    fun handlePlayerControl(play: Boolean)
    fun addVideos(video: CameraFull)
}