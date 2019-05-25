package ru.security.live.presentation.view.ui.activity

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_archive.*
import kotlinx.android.synthetic.main.layout_player.*
import kotlinx.android.synthetic.main.radio_seek_bar.*
import kotlinx.android.synthetic.main.range_date.view.*
import kotlinx.android.synthetic.main.range_time.view.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull
import ru.security.live.domain.entity.EventDetailed
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.ArchivePresenter
import ru.security.live.presentation.presenter.EventPresenter
import ru.security.live.presentation.view.iview.ArchiveView
import ru.security.live.presentation.view.iview.EventView
import ru.security.live.util.*
import ru.security.live.util.DateTool.Companion.format
import ru.security.live.util.DateTool.Companion.fullDateSec
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.util.*
import java.util.concurrent.TimeUnit
/**
 * @author sardor
 */
class ArchiveActivity : BaseActivity(), ArchiveView, EventView,
        TextureView.SurfaceTextureListener, View.OnClickListener {


    @InjectPresenter
    lateinit var presenter: ArchivePresenter


    @InjectPresenter
    lateinit var eventPresenter: EventPresenter

    @ProvidePresenter
    fun providesPresenter(): EventPresenter {
        return EventPresenter(intent.getStringExtra(EXTRA_EVENT_ID))
    }


    private val archiveStartCalendar = Calendar.getInstance()
    private val archiveEndCalendar = Calendar.getInstance()
    private val filterStartCalendar = Calendar.getInstance()
    private var archive: ArchiveItem? = null
    private var camera: CameraFull? = null

    private var mFilePath: String = ""
    private var player: IjkMediaPlayer? = null

    private var indicatorPosition = 0
    private var rangeInMillis: Long = 0
    private var millisPerPixel: Long = 0
    private var disposable: Disposable? = null
    private var surfaceReady = false
    private var isStreamReady = false
    private var timer: CountDownTimer? = null
    private var videoResized = false
    private var cvVideoResized = false
    private var controlButtonClicked = false
    private var isFromEvent = false
    private var surface: Surface? = null
    private var isSmallTime = false
    var isMap = false
    lateinit var videoProgress: View

    private val permissions by lazy {
        RxPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_archive)
        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.archive)
        videoProgress = LayoutInflater.from(this).inflate(R.layout.progress, null)
        try {
            archive = intent.getSerializableExtra(INTENT_ARCHIVE_INFO) as ArchiveItem
            camera = intent.getSerializableExtra(INTENT_CAMERA_INFO) as CameraFull
        } catch (e: ClassCastException) {
            print(e.localizedMessage)
        }

        val eventId = intent.getStringExtra(EXTRA_EVENT_ID)
        if (eventId == null) {
            setupArchive()
        } else {
            isMap = true
        }

    }

    fun setupArchive() {

        mFilePath = archive?.url!!
        archiveStartCalendar.time = archive?.getStart()
        archiveEndCalendar.time = archive?.getEnd()

        LoggingTool.log("Archive time: ${archiveStartCalendar.get(Calendar.HOUR_OF_DAY)}:" +
                "${archiveStartCalendar.get(Calendar.MINUTE)}")

        LoggingTool.log("Archive time: ${archiveEndCalendar.get(Calendar.HOUR_OF_DAY)}:" +
                "${archiveEndCalendar.get(Calendar.MINUTE)}")

        presenter.endCalendar = archiveEndCalendar

        isFromEvent = intent.getBooleanExtra(INTENT_ARCHIVE_IS_EVENT, false)


        tvToolbar.text = camera?.name
        tvLive.setOnClickListener(this)
        tvCalendar.setOnClickListener(this)
        tvUserEvent.setOnClickListener(this)
        btnControl.setOnClickListener(this)
        ivFullscreen.setOnClickListener(this)

        cvVideo.viewTreeObserver.addOnGlobalLayoutListener {
            //LoggingTool.log("cvVideo onGlobalLayoutListener $videoResized")
            if (!cvVideoResized) {
                val params = cvVideo.layoutParams
                params.height = (cvVideo.measuredWidth / 1.38).toInt()
                cvVideo.layoutParams = params

                val params2 = containerFullscreen.layoutParams
                params2.height = (params.height / 8.6).toInt()
                containerFullscreen.layoutParams = params2
                setUpTexture()
                cvVideoResized = true
            }
        }

        indicator.viewTreeObserver.addOnGlobalLayoutListener {
            if (indicatorPosition == 0) {
                indicatorPosition = indicator.x.toInt()

                LoggingTool.log("Indicator position on global ${indicator.x.toInt()}")
                generateScale(archiveStartCalendar, archiveEndCalendar)
            }
        }
        enableNavButtons(false)
        LoggingTool.log("From ${archive?.from} to ${archive?.to}")



        video.isOpaque = false
        video.setOnTouchListener { v, event ->
            LoggingTool.log("video onTouchListener")
            btnControl.visibility = View.VISIBLE
            containerFullscreen.visibility = View.VISIBLE
            Handler().postDelayed({
                if (!controlButtonClicked) {
                    btnControl.visibility = View.GONE
                    containerFullscreen.visibility = View.GONE
                }
            }, 5000)
            false
        }
        video.surfaceTextureListener = this



        updateLink(archive?.url!!)
    }

    override fun onClick(v: View?) {
        when (v) {
            tvLive -> {
                Navigator.navigateToVideo(this, camera?.id!!)
            }
            tvCalendar -> showDatePicker(archive?.getStart()?.time!!, archive?.getEnd()?.time!!)
            tvUserEvent -> {
                permissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe {
                            if (it) {
                                createUserEvent()
                            } else {
                                error(Throwable(getString(R.string.noPermission)))
                            }
                        }
            }
            btnControl -> handlePlayerControl(!player?.isPlaying!!)
            ivFullscreen -> {
                Navigator.navigateToFullscreenVideo(mFilePath, this@ArchiveActivity)
                releasePlayer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        disposable?.dispose()
        timer?.cancel()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isMap)
            MenuInflater(this).inflate(R.menu.video_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (isMap)
            return super.onOptionsItemSelected(item)
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.video_menu_item -> {
                Navigator.navigateToDesktops(this, true)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null && data.hasExtra(INTENT_ARCHIVE_CAMERA_URL)) {
            mFilePath = data.getStringExtra(INTENT_ARCHIVE_CAMERA_URL)
            initPlayer(mFilePath)
        }
    }

    override fun onBackPressed() {
        presenter.onDestroy()
        super.onBackPressed()
    }

    override fun createUserEvent() {
        val path = presenter.saveFile(this, video.bitmap)

        //val image = Bitmap.createScaledBitmap(video.bitmap, video.width / 5, video.height / 5, true)
        val params = AddEventActivity.Params(camera?.name!!, camera?.location!!, path)
        Navigator.navigateToAddEvent(this, params)
    }

    override fun updateLink(media: String) {
        showVideoLoading()
        mFilePath = media
        if (video.isAvailable)
            initPlayer(mFilePath)
        else video.surfaceTextureListener = this
    }

    override fun initPlayer(media: String) {
        tvTimeout.visibility = View.GONE
        enableNavButtons(false)
        LoggingTool.log("Online rtmp $media")
        isStreamReady = false

        SurfaceHelper.clearSurface(surface)

        timer = object : CountDownTimer(1 * 60 * 1000, 1000) {
            override fun onFinish() {
                if (!isStreamReady) {
                    hideVideoLoading()
                    tvTimeout.visibility = View.VISIBLE
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
            SurfaceHelper.adjustAspectRatio(width, height, video, null)
        }
        player?.setSurface(surface)
        player?.setDataSource(media, null)
        player?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        player?.setOnPreparedListener {
            enableNavButtons(true)
            hideVideoLoading()
            player?.start()
            isStreamReady = true
            timer?.cancel()
            if (isFromEvent) {
                scrollScaleToDate(getCalendarFromTextDate(archive?.from!!).timeInMillis)
                isFromEvent = false
            }
        }
        player?.setOnBufferingUpdateListener { mp, percent ->
            LoggingTool.log("buffered $percent player ${mp.isPlaying}")
        }
        player?.setOnErrorListener { _, what, extra ->
            hideVideoLoading()
            LoggingTool.log("Player error $what $extra")
            if (what != -10000)
                error(Throwable(getString(R.string.broadcastError)))
            false
        }
        player?.prepareAsync()
    }

    override fun releasePlayer() {
        player?.reset()
        player?.release()
        player?.setSurface(null)
        player = null
    }

    override fun showDatePicker(start: Long, end: Long) {
        val dialog = DatePickerDialog(this,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    filterStartCalendar.set(Calendar.YEAR, year)
                    filterStartCalendar.set(Calendar.MONTH, month)
                    filterStartCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    filterStartCalendar.set(Calendar.HOUR_OF_DAY, 0)
                    filterStartCalendar.set(Calendar.MINUTE, 0)
                    filterStartCalendar.set(Calendar.SECOND, 0)
                    filterStartCalendar.set(Calendar.MILLISECOND, 0)


                    if (dayOfMonth == archiveStartCalendar.get(Calendar.DAY_OF_MONTH)) {
                        filterStartCalendar.set(Calendar.HOUR_OF_DAY,
                                archiveStartCalendar.get(Calendar.HOUR_OF_DAY))
                        filterStartCalendar.set(Calendar.MINUTE,
                                archiveStartCalendar.get(Calendar.MINUTE))
                        filterStartCalendar.set(Calendar.SECOND,
                                archiveStartCalendar.get(Calendar.SECOND))
                        filterStartCalendar.set(Calendar.MILLISECOND,
                                archiveStartCalendar.get(Calendar.MILLISECOND))
                    }

                    showTimePicker(filterStartCalendar)
                },
                archiveStartCalendar.get(Calendar.YEAR),
                archiveStartCalendar.get(Calendar.MONTH),
                archiveStartCalendar.get(Calendar.DAY_OF_MONTH))

        dialog.datePicker.minDate = start
        dialog.datePicker.maxDate = end
        dialog.show()
    }

    override fun showTimePicker(calendar: Calendar) {
        val dialog = TimePickerDialog(this,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    filterStartCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    filterStartCalendar.set(Calendar.MINUTE, minute)

                    if (filterStartCalendar.before(archiveStartCalendar)) {
                        filterStartCalendar.set(Calendar.HOUR_OF_DAY, getHour(archiveStartCalendar))
                        filterStartCalendar.set(Calendar.MINUTE, getMinute(archiveStartCalendar))
                    } else if (filterStartCalendar.after(archiveEndCalendar)) {
                        filterStartCalendar.set(Calendar.HOUR_OF_DAY, getHour(archiveEndCalendar) - 1)
                        filterStartCalendar.set(Calendar.MINUTE, 0)
                    }

                    val from = DateTool.formatForRequest(filterStartCalendar)
                    presenter.getStream(camera?.id!!, from, archive?.to!!)
                    scrollScaleToDate(filterStartCalendar.timeInMillis)
                },
                0,
                0,
                true)
        dialog.show()
    }

    private fun generateScale(start: Calendar, end: Calendar) {
        val scale = LinearLayout(this)
        scale.orientation = LinearLayout.HORIZONTAL
        scale.gravity = Gravity.CENTER_VERTICAL

        val tempCalendar = Calendar.getInstance()
        tempCalendar.time = start.time

        rangeInMillis = end.timeInMillis - start.timeInMillis
        val result = 1.0 * rangeInMillis / 1000.0 / 60.0 / 60.0
        LoggingTool.log("result$result")
        var rangeInHours = Math.round(result)
        var firstHour = start.get(Calendar.HOUR_OF_DAY)

        LoggingTool.log("From ${format(start, fullDateSec)} to ${format(end, fullDateSec)}")

        if (rangeInHours >= 24) {
            if (firstHour != 12 && firstHour != 0) {
                var difference = if (firstHour < 12) 12 - firstHour else 24 - firstHour

                repeat(difference) {
                    scale.addView(layoutInflater.inflate(R.layout.scale_small, null))
                }
                LoggingTool.log("Difference $difference")
                rangeInHours -= difference
                firstHour += difference

                if (firstHour > 23) firstHour -= 24
            }

            LoggingTool.log("New hours: $rangeInHours (should be multiple of 12)")
            LoggingTool.log("New firstHour: $firstHour (should be either 12 or 24)")

            if (firstHour == 12) {
                scale.addView(layoutInflater.inflate(R.layout.range_time, null))
                firstHour = 0
                rangeInHours -= 12
            }

            tempCalendar.set(Calendar.DAY_OF_MONTH, start.get(Calendar.DAY_OF_MONTH) + if (start.get(Calendar.HOUR_OF_DAY) != 0) 1 else 0)
            tempCalendar.set(Calendar.HOUR_OF_DAY, 0)

            val remainder = (rangeInHours % 12).toInt() + 1
            val halfDays = (rangeInHours / 12).toInt()

            LoggingTool.log("Half days $halfDays")
            LoggingTool.log("Remainder $remainder")

            repeat(halfDays) {
                val hour = tempCalendar.get(Calendar.HOUR_OF_DAY)
                if (hour == 12) {
                    scale.addView(layoutInflater.inflate(R.layout.range_time, null))
                    tempCalendar.set(Calendar.DAY_OF_MONTH, tempCalendar.get(Calendar.DAY_OF_MONTH) + 1)
                    tempCalendar.set(Calendar.HOUR_OF_DAY, 0)
                } else {
                    val dateView = layoutInflater.inflate(R.layout.range_date, null)
                    dateView.day.text = format(tempCalendar, "dd MMM")
                    scale.addView(dateView)
                    tempCalendar.set(Calendar.HOUR_OF_DAY, 12)
                }
            }

            if (remainder > 0) {
                val hour = tempCalendar.get(Calendar.HOUR_OF_DAY)
                if (hour == 12) {
                    val timeView = layoutInflater.inflate(R.layout.range_time, null)
                    val scaleHolder = timeView.scaleHolder2
                    scaleHolder.removeViews(remainder, 12 - remainder)
                    scale.addView(timeView)
                } else {
                    val dateView = layoutInflater.inflate(R.layout.range_date, null)
                    dateView.day.text = format(tempCalendar, "dd MMM")
                    val scaleHolder = dateView.scaleHolder
                    scaleHolder.removeViews(remainder, 12 - remainder)
                    scale.addView(dateView)
                }
            }
        } else {
            val endMillis = end.timeInMillis
            val startMillis = start.timeInMillis
            var differenceInMinutes = (((endMillis - startMillis) / 1000) / 60).toInt()
            // Sometimes start's and end's hours are the same, they only differ in minutes(16:34 and 16:44)
            if (60 >= differenceInMinutes) {
                isSmallTime = true
                var durationContainer = layoutInflater.inflate(R.layout.range_only_time, null)
                val innerScale = durationContainer.findViewById<LinearLayout>(R.id.scaleHolder)

                LoggingTool.log("Difference $differenceInMinutes")
                if (differenceInMinutes == 0)
                    differenceInMinutes = 1
                repeat(differenceInMinutes) {
                    innerScale.addView(layoutInflater.inflate(R.layout.scale_small, null))
                }

                val startTime = durationContainer.findViewById<TextView>(R.id.txtStartTime)
                val endTime = durationContainer.findViewById<TextView>(R.id.txtEndTime)

                startTime.text = format(start, "HH:mm")
                endTime.text = format(end, "HH:mm")
                if (differenceInMinutes < 8) {
                    startTime.visibility = View.GONE
                    endTime.visibility = View.GONE
                }
                scale.addView(durationContainer)
                addScaleToScreen(scale, start)
                return
            }

            if (firstHour != 12 && firstHour != 0) {
                val difference = if (firstHour < 12) 12 - firstHour else 24 - firstHour
                LoggingTool.log("Abd difference:$difference")
                repeat(difference) {
                    scale.addView(layoutInflater.inflate(R.layout.scale_small, null))
                }
                LoggingTool.log("Difference $difference")
                rangeInHours -= difference
                firstHour += difference

                if (firstHour > 23) firstHour -= 24

                tempCalendar.timeInMillis += difference * 60 * 60 * 1000
            }

            val remainder = (rangeInHours % 12).toInt()
            val halfDays = (rangeInHours / 12).toInt()
            LoggingTool.log("remainder:$remainder; halfdays:$halfDays")

            repeat(halfDays) {
                if (firstHour == 12) {
                    scale.addView(layoutInflater.inflate(R.layout.range_time, null))
                    tempCalendar.set(Calendar.DAY_OF_MONTH, tempCalendar.get(Calendar.DAY_OF_MONTH) + 1)
                    tempCalendar.set(Calendar.HOUR_OF_DAY, 0)
                } else {
                    val dateView = layoutInflater.inflate(R.layout.range_date, null)
                    dateView.day.text = format(tempCalendar, "dd MMM")
                    scale.addView(dateView)
                    tempCalendar.set(Calendar.HOUR_OF_DAY, 12)
                }
            }

            if (remainder > 0) {
                val hour = tempCalendar.get(Calendar.HOUR_OF_DAY)
                if (hour == 12) {
                    val timeView = layoutInflater.inflate(R.layout.range_time, null)
                    val scaleHolder = timeView.scaleHolder2
                    scaleHolder.removeViews(remainder, 12 - remainder)
                    scale.addView(timeView)
                } else {
                    val dateView = layoutInflater.inflate(R.layout.range_date, null)
                    dateView.day.text = format(tempCalendar, "dd MMM")
                    val scaleHolder = dateView.scaleHolder
                    scaleHolder.removeViews(remainder
                            , 12 - remainder)
                    scale.addView(dateView)
                }
            }
        }

        addScaleToScreen(scale, start)
    }

    private fun addScaleToScreen(scale: LinearLayout, start: Calendar) {
        scaleContainer.removeAllViews()

        val blankStart = layoutInflater.inflate(R.layout.range_blank, null)
        blankStart.minimumWidth = indicatorPosition

        val blankEnd = layoutInflater.inflate(R.layout.range_blank, null)
        blankEnd.minimumWidth = indicatorPosition

        scaleContainer.addView(blankStart)
        scaleContainer.addView(blankEnd)

        scaleContainer.addView(scale, 1)
        Handler().postDelayed({
            val scaleLayout = scaleContainer.getChildAt(1) as LinearLayout
            var width = scaleLayout.measuredWidth
            millisPerPixel = rangeInMillis / width
            scheduleAutoScroll()
        }, 500)

        scrollViewSeekBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val seekPosition = v.scrollX

                val timestamp = start.timeInMillis + (seekPosition * millisPerPixel) + if (isSmallTime) 4 * 1000 else 0
                val date = DateTool.formatForRequest(timestamp, DateTool.fullDateMsNoZ)
                LoggingTool.log("Start data: $date")

                presenter.getStream(camera?.id!!, date, archive?.to!!)
                scheduleAutoScroll()
            }
            false
        }
    }

    override fun setUpScale(calendarStart: Calendar) {
        val scaleContainer = scrollViewSeekBar.getChildAt(0) as LinearLayout
        val scaleWidth = scaleContainer.getChildAt(1).measuredWidth
        LoggingTool.log("scaleWidth $scaleWidth")
        millisPerPixel = rangeInMillis.div(scaleWidth)

        //При нажатии на шкалу генерируем момент для перемотки (после поднятия пальца)
        scrollViewSeekBar.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val seekPosition = v.scrollX
                archive?.getStart()
                val timestamp = calendarStart.timeInMillis + (seekPosition * millisPerPixel) + if (isSmallTime) 4 * 1000 else 0
                val date = DateTool.formatForRequest(timestamp, DateTool.fullDateMsNoZ)

                LoggingTool.log("Start data $date")

                presenter.getStream(camera?.id!!, date, archive?.to!!)
                scheduleAutoScroll()
            }
            false
        }
        scheduleAutoScroll()
    }

    override fun scheduleAutoScroll() {
        //Скроллим шкалу на 1 пиксель каждые x миллисекунд, исходя из длительности видео
        disposable = Observable.just(scrollViewSeekBar)
                .observeOn(AndroidSchedulers.mainThread())
                .repeatWhen {
                    it.delay(millisPerPixel, TimeUnit.MILLISECONDS)
                }
                .subscribe {
                    player?.let { play ->
                        if (play.isPlaying)
                            it.scrollTo(it.scrollX + 1, 0)
                    }
                }

    }

    override fun scrollScaleToDate(timestamp: Long) {
        val difference = timestamp - archiveStartCalendar.timeInMillis
        val position = difference / millisPerPixel
        scrollViewSeekBar.scrollTo(position.toInt(), 0)
    }

    override fun pausePlayer() {
        player?.pause()
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

    override fun enableNavButtons(enable: Boolean) {
        ivFullscreen.isEnabled = enable
        tvLive.isEnabled = enable
        tvCalendar.isEnabled = enable
        tvUserEvent.isEnabled = enable

        ivFullscreen.isClickable = enable
        tvLive.isClickable = enable
        tvCalendar.isClickable = enable
        tvUserEvent.isClickable = enable
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {}

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {}

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?) = false

    override fun onSurfaceTextureAvailable(surfaceTexture: SurfaceTexture?, width: Int, height: Int) {
        surface = Surface(surfaceTexture)
        surfaceReady = true
        initPlayer(mFilePath)
    }

    fun getHour(calendar: Calendar): Int {
        return calendar.get(Calendar.HOUR_OF_DAY)
    }

    fun getMinute(calendar: Calendar): Int {
        return calendar.get(Calendar.MINUTE)
    }

    fun getDay(calendar: Calendar): Int {
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    fun getMonth(calendar: Calendar): Int {
        return calendar.get(Calendar.MONTH)
    }

    override fun error(error: Throwable) {
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun showVideoLoading() {
        archiveProgress.visibility = View.VISIBLE
    }

    override fun hideVideoLoading() {
        archiveProgress.visibility = View.GONE
    }

    override fun setUpTexture() {
        video.viewTreeObserver.addOnGlobalLayoutListener {
            if (!videoResized) {
                val params = video.layoutParams
                val height = (video.measuredWidth / 1.38).toInt()
                LoggingTool.log("Video height:$height")
                params.height = height
                video.layoutParams = params

                val params2 = containerFullscreen.layoutParams
                val containerHeight = (params.height / 8.6).toInt()
                params2.height = containerHeight
                LoggingTool.log("Container height:$containerHeight")
                containerFullscreen.layoutParams = params2
                videoResized = true
            }
        }
        video.isOpaque = false
        video.setOnTouchListener { v, event ->
            LoggingTool.log("video onTouchListener")
            btnControl.visibility = View.VISIBLE
            containerFullscreen.visibility = View.VISIBLE
            Handler().postDelayed({
                if (!controlButtonClicked) {
                    btnControl.visibility = View.GONE
                    containerFullscreen.visibility = View.GONE
                }
            }, 5000)
            false
        }
        video.surfaceTextureListener = this
    }

    override fun removeTexture() {
        containerContent.removeViewAt(1)
        addTexture()
    }

    override fun addTexture() {
        val playerView = layoutInflater.inflate(R.layout.layout_player, null)
        (containerContent as ViewGroup).addView(playerView, 1)
        setUpTexture()
    }


    override fun handleVisibility(arrow: ImageView, details: Array<View>) {
        // we dont need this APIs
    }

    override fun openElement(arrow: ImageView, details: Array<View>) {
        // we dont need this APIs
    }

    override fun closeElement(arrow: ImageView, details: Array<View>) {
        // we dont need this APIs
    }

    override fun setUpMap(device: EventDetailed.Device) {
        // we dont need this APIs
    }

    override fun showDetails(event: EventDetailed, camera: CameraFull?, archives: List<ArchiveItem>) {
        if (camera == null || archives.isEmpty()) {
            groupEmptyView.visibility = View.VISIBLE
            containerContent.visibility = View.GONE
            return
        }
        this.camera = camera
        this.archive = archives[0]
        setupArchive()
    }

}
