package ru.security.live.presentation.view.iview
/**
 * @author sardor
 */
interface FullscreenVideoView {
    fun initPlayer(media: String)
    fun releasePlayer()
    fun showVideoLoading()
    fun hideVideoLoading()
    fun handlePlayerControl(play: Boolean)
}