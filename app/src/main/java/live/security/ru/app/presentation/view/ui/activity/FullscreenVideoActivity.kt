package ru.security.live.presentation.view.ui.activity

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_fullscreen_video.*
import ru.security.live.R
import ru.security.live.presentation.view.iview.FullscreenVideoView
import ru.security.live.util.INTENT_RTMP
import ru.security.live.util.LoggingTool
import ru.security.live.util.SurfaceHelper
import tv.danmaku.ijk.media.player.IjkMediaPlayer
/**
 * @author sardor
 */
class FullscreenVideoActivity : BaseActivity(), FullscreenVideoView,
        TextureView.SurfaceTextureListener {

    private var mFilePath: String = ""

    private var player: IjkMediaPlayer? = null
    private var surfaceReady = false
    private var isStreamReady = false
    private var timer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_video)
//        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mFilePath = intent.getStringExtra(INTENT_RTMP)
        video.surfaceTextureListener = this
        video.viewTreeObserver.addOnGlobalLayoutListener {
            //            if (!videoResized){
//                LoggingTool.log("Width = ${video.measuredWidth}")
//                val params = video.layoutParams
//                params.height = (video.measuredWidth / 1.38).toInt()
//                video.layoutParams = params
//                videoResized = true
//            }
        }
        btnControl.setOnClickListener {
            handlePlayerControl(!player?.isPlaying!!)
        }
        ivFullscreen.setOnClickListener {
            releasePlayer()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        if (mFilePath.isNotEmpty() && video.isAvailable)
            initPlayer(mFilePath)
        else
            video.surfaceTextureListener = this
    }

    override fun initPlayer(media: String) {
        LoggingTool.log("Online rtmp $media")
        showVideoLoading()

        player = IjkMediaPlayer()
        player?.setOnVideoSizeChangedListener { mp, width, height, sar_num, sar_den ->
            SurfaceHelper.adjustAspectRatio(width, height, video, null)
        }
        player?.setSurface(Surface(video.surfaceTexture))
        player?.setDataSource(media, null)
        player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player?.setOnPreparedListener {
            hideVideoLoading()
            player?.start()
            timer?.cancel()
        }
        player?.setOnErrorListener { _, what, extra ->
            hideVideoLoading()
            false
        }
        player?.prepareAsync()
        timer = object : CountDownTimer(1 * 60 * 1000, 1000) {
            override fun onFinish() {
                if (!isStreamReady) {
                    hideVideoLoading()
                    releasePlayer()
                    handlePlayerControl(false)
                    error(Throwable(getString(R.string.broadcastTimeout)))
                }
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }
        timer?.start()
    }

    override fun releasePlayer() {
        player?.reset()
        player?.release()
        player = null
    }

    override fun showVideoLoading() {
        progressBarFullVideo.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun hideVideoLoading() {
        progressBarFullVideo.visibility = View.GONE
    }

    override fun handlePlayerControl(play: Boolean) {
        if (play) {
            if (player != null)
                player?.start()
            else
                initPlayer(mFilePath)
        } else {
            player?.pause()
        }

        btnControl.setImageResource(if (play) R.drawable.button_pause else R.drawable.button_play)

        if (play) {
            btnControl.postDelayed({
                btnControl.visibility = View.GONE
            }, 5000)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        surfaceReady = true
        initPlayer(mFilePath)
    }
}
