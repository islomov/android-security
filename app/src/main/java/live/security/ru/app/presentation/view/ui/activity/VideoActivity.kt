package ru.security.live.presentation.view.ui.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_video.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.CameraFull
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.VideosPresenter
import ru.security.live.presentation.view.iview.VideoView
import ru.security.live.presentation.view.ui.adapters.VideosAdapter
import ru.security.live.util.*
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.util.concurrent.TimeUnit

/**
 * @author sardor
 */
class VideoActivity : BaseActivity(), VideoView,
        TextureView.SurfaceTextureListener, View.OnClickListener {

    companion object {
        const val typeDesktop = 124
        const val typeCamera = 123
    }

    @InjectPresenter
    lateinit var presenter: VideosPresenter
    private lateinit var adapterVideos: VideosAdapter
    private var currentCamera = CameraFull()
    private var disposable: Disposable? = null

    private var mFilePath: String = ""
    private var player: IjkMediaPlayer? = null
    private var surfaceReady = false
    private var videoResized = false
    private var isStreamReady = false
    private var timer: CountDownTimer? = null

    private var controlButtonClicked = false
    private var cameraIndex = 0
    private val cameraList = ArrayList<CameraFull>()
    private var broadcastAvailable = true
    private var handler = Handler()
    private var currentPlayingStream = ""
    private var surfaceView: Surface? = null
    private var isFirstVideoSet = false
    private val permissions by lazy {
        RxPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.cctv)
        tvArchive.setOnClickListener(this)
        tvUserEvent.setOnClickListener(this)
        btnControl.setOnClickListener(this)
        ivFullscreen.setOnClickListener(this)

        setUpPlayer()

        val type = intent.getIntExtra(INTENT_VIDEO_TYPE, typeDesktop)

        if (type == typeDesktop) {
            setUpDesktop()
        } else {
            val id = intent.getStringExtra(INTENT_CAMERA_INFO)
            presenter.getCamera(id)
        }

        enableNavButtons(false)
    }

    private fun setUpPlayer() {
        video.isOpaque = true
        video.surfaceTextureListener = this
        video.viewTreeObserver.addOnGlobalLayoutListener {
            if (!videoResized) {
                LoggingTool.log("Width = ${video.measuredWidth}")
                val params = video.layoutParams
                params.height = (video.measuredWidth / 1.38).toInt()
                video.layoutParams = params
                videoResized = true
            }
        }

        video.setOnTouchListener(object : OnSwipeTouchListener(this) {
            //Переключение на другую камеру по свайпу на плеере
            override fun onSwipeRight() {
                super.onSwipeRight()
//                cameraIndex++
//                if (cameraIndex >= cameraList.size) cameraIndex = 0
//                setCurrentVideo(cameraList[cameraIndex])
            }

            override fun onSwipeLeft() {
                super.onSwipeLeft()
//                cameraIndex--
//                if (cameraIndex < 0) cameraIndex = cameraList.size - 1
//                setCurrentVideo(cameraList[cameraIndex])
            }

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_UP) {
//                    btnControl.visibility = View.VISIBLE
                    containerFullscreen.visibility = View.VISIBLE
                    Handler().postDelayed({
                        if (!controlButtonClicked) {
//                            btnControl.visibility = View.GONE
                            containerFullscreen.visibility = View.GONE
                        }
                    }, 5000)
                }
                return super.onTouch(v, event)
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            tvArchive -> onArchiveChecked()
            tvUserEvent -> permissions
                    .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .subscribe({
                        if (it) {
                            createUserEvent()
                        } else {
                            error(Throwable(getString(R.string.noPermission)))
                        }
                    }, { err -> })
            btnControl -> if (player != null) handlePlayerControl(player!!.isPlaying)
            ivFullscreen -> {
                Navigator.navigateToFullscreenVideo(mFilePath, this@VideoActivity)
                releasePlayer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::adapterVideos.isInitialized) {
            disposable = Observable.just(adapterVideos)
                    .observeOn(AndroidSchedulers.mainThread())
                    .repeatWhen { it.delay(15, TimeUnit.SECONDS) }
                    .subscribe { adapterVideos.notifyDataSetChanged() }
        }

        if (mFilePath.isNotEmpty() && video.isAvailable)
            initPlayer(mFilePath)
        else
            video.surfaceTextureListener = this
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        presenter.onDestroy()
        timer?.cancel()
    }

    override fun onStop() {
        super.onStop()
        surfaceReady = false
        releasePlayer()
        timer?.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!intent.getBooleanExtra(INTENT_FROM_CAMERA, false) &&
                !intent.getBooleanExtra(INTENT_FROM_EVENT, false) && !intent.getBooleanExtra(INTENT_VIDEO_FROM_MAP, false))
            MenuInflater(this).inflate(R.menu.video_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (::adapterVideos.isInitialized)
                    adapterVideos.recycleBitmaps()
                finish()
            }
            R.id.video_menu_item -> {
                Navigator.navigateToDesktops(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        LoggingTool.log("Video Activity - onActivityResult")
        if (data != null && data.hasExtra(INTENT_DESKTOP_POSITION)) {
            val desktopPosition = data.getIntExtra(INTENT_DESKTOP_POSITION, 0)
            cameraList.clear()
            isFirstVideoSet = false
            tvTitle.text = ""
            adapterVideos.submitList(cameraList)
            videoPlaceHolder.visibility = View.VISIBLE
            presenter.getParallelCameras(desktopPosition)
        } else if (data != null && data.hasExtra(INTENT_VIDEO_TYPE)) {
            currentPlayingStream = "//restart"
            showVideoLoading()
            setCurrentVideo(cameraList[cameraIndex])
        }
    }

    override fun onRestart() {
        super.onRestart()
        currentPlayingStream = "//restart"
        showVideoLoading()
        if (cameraList.isNotEmpty())
            setCurrentVideo(cameraList[cameraIndex])
    }

    override fun onBackPressed() {
        if (::adapterVideos.isInitialized)
            adapterVideos.recycleBitmaps()
        presenter.onDestroy()
        releasePlayer()
        super.onBackPressed()
    }

    override fun setUpDesktop() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterVideos = VideosAdapter(this@VideoActivity::clickOnVideoItem, resources.getString(R.string.noConnection))
        val decoration = DividerItemDecoration(rvVideos.context, layoutManager.orientation)
        decoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider)!!)

        rvVideos.layoutManager = layoutManager
        rvVideos.addItemDecoration(decoration)
        rvVideos.adapter = adapterVideos
        rvVideos.visibility = View.VISIBLE

        val desktopPosition = intent.getIntExtra(INTENT_DESKTOP_POSITION, 0)
        presenter.getParallelCameras(desktopPosition)
    }

    override fun setVideos(list: List<CameraFull>) {
        cameraList.clear()
        cameraList.addAll(list)
        cameraIndex = 0
        setCurrentVideo(list.first())
        presenter.setCurrentVideo(list.first())
        refreshVideoList(list)
    }

    override fun addVideos(video: CameraFull) {
        cameraList.add(video)
        adapterVideos.addItem(video)
        if (!isFirstVideoSet) {
            cameraIndex = 0
            setCurrentVideo(video)
            presenter.setCurrentVideo(video)
            isFirstVideoSet = true
        }

    }

    //Для получения скриншотов
    override fun refreshVideoList(list: List<CameraFull>) {
        adapterVideos.submitList(list)
        disposable = Observable.just(adapterVideos)
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen { it.delay(15, TimeUnit.SECONDS) }
                .subscribe { adapterVideos.notifyDataSetChanged() }
    }

    override fun onArchiveChecked() {
        LoggingTool.log("onArchiveChecked:${currentCamera.archiveAvailable}")
        if (currentCamera.archiveAvailable) {
            Navigator.navigateToArchive(this, currentCamera, currentCamera.archive!!)
        } else AlertDialog.Builder(this)
                .setMessage("Для данной камеры отсутствую архивные записи")
                .show()
    }

    private fun clickOnVideoItem(item: CameraFull, position: Int) {
        cameraIndex = position
        setCurrentVideo(item)
        presenter.setCurrentVideo(item)
    }

    override fun setCurrentVideo(cameraFull: CameraFull) {
        tvTimeout.text = ""
        releasePlayer()
        currentCamera = cameraFull
        LoggingTool.log("Current video:${cameraFull.url}")
        tvTitle.text = cameraFull.name
        mFilePath = if (!cameraFull.url.isNullOrEmpty()) cameraFull.url!! else ""
        broadcastAvailable = mFilePath.isNotEmpty()
        handler.removeCallbacks(openArchiveRunnable())

        if (mFilePath.isNotEmpty() && video.isAvailable) {
            initPlayer(mFilePath)
        } else {
            tvTimeout.text = getString(R.string.invalidBroadcast)
            if (cameraFull.archiveAvailable) {
                Toast.makeText(this, R.string.archiveRedirectMessage, Toast.LENGTH_LONG).show()
                handler.postDelayed(openArchiveRunnable(), 3000)
            }
        }
    }

    private fun openArchiveRunnable(): Runnable {
        return Runnable {
            if (!broadcastAvailable) {
                onArchiveChecked()
                finish()
            }
        }
    }

    override fun initPlayer(media: String) {
        if (media.isEmpty() || media == currentPlayingStream)
            return
        if (currentPlayingStream.isNotEmpty())
            showVideoPlaceholder()
        currentPlayingStream = media
        tvTimeout.text = ""
        enableNavButtons(false)
        LoggingTool.log("Online rtmp $media")
        showVideoLoading()
        SurfaceHelper.clearSurface(surfaceView)
        isStreamReady = false
        timer = object : CountDownTimer(1 * 60 * 1000, 1000) {
            override fun onFinish() {
                if (!isStreamReady) {
                    hideVideoLoading()
                    tvTimeout.text = getString(R.string.broadcastTimeout)
                    releasePlayer()
                    handlePlayerControl(false)
                }
            }

            override fun onTick(millisUntilFinished: Long) {

            }
        }
        timer?.start()

        player = IjkMediaPlayer()

        player?.setOnVideoSizeChangedListener { mp, width, height, sar_num, sar_den ->
            LoggingTool.log("Video W $width H $height")
            SurfaceHelper.adjustAspectRatio(width, height, video, videoPlaceHolder)
        }
        player?.reset()
        player?.setSurface(surfaceView)
        player?.setDataSource(media, null)
        player?.setOnCompletionListener { }
        player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player?.setOnPreparedListener {
            isStreamReady = true
            enableNavButtons(true)
            player?.start()
            timer?.cancel()
            hideVideoPlaceholder()
        }
        player?.setOnErrorListener { _, what, extra ->
            hideVideoLoading()
            LoggingTool.log("Player error $what $extra")
            false
        }
        player?.prepareAsync()
    }

    override fun releasePlayer() {

        player?.reset()
        player?.release()
        player = null
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
        return false
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
        surfaceView = Surface(surface)
        surfaceReady = true
        initPlayer(mFilePath)
    }

    override fun showVideoLoading() {
        cardProgress.visibility = View.VISIBLE
    }

    override fun hideVideoLoading() {
        cardProgress.visibility = View.GONE
    }

    override fun createUserEvent() {
        val path = presenter.saveFile(this, video.bitmap)
        //val image = Bitmap.createScaledBitmap(videoSection.bitmap, (videoSection.width / 2).toInt(), (videoSection.height / 2).toInt(), false)
        val params = AddEventActivity.Params(currentCamera.name, currentCamera.location, path)
        Navigator.navigateToAddEvent(this, params)
    }

    override fun enableNavButtons(enable: Boolean) {
        ivFullscreen.isEnabled = enable
        tvArchive.isEnabled = enable
        tvUserEvent.isEnabled = enable
    }

    override fun handlePlayerControl(play: Boolean) {
        controlButtonClicked = true
        if (play) {
            if (player != null)
                player?.start()
            else
                initPlayer(mFilePath)
        } else {
            player?.pause()
        }

        btnControl.setImageResource(if (play) R.drawable.button_pause
        else R.drawable.button_play)

        Handler().postDelayed({
            controlButtonClicked = false
            btnControl.visibility = View.GONE
            containerFullscreen.visibility = View.GONE
        }, 5000)
    }

    fun showVideoPlaceholder() {
        videoPlaceHolder.visibility = View.VISIBLE
    }

    fun hideVideoPlaceholder() {
        Handler().postDelayed({
            hideVideoLoading()
            videoPlaceHolder.visibility = View.GONE
        }, 500)
    }


}