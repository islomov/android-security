package ru.security.live.presentation.view.ui.activity

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.animation.Animation
import android.widget.ImageView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.grn_data_layout.*
import kotlinx.android.synthetic.main.money_table.*
import kotlinx.android.synthetic.main.toolbar.*
import org.osmdroid.util.GeoPoint
import ru.security.live.R
import ru.security.live.domain.entity.*
import ru.security.live.domain.entity.enums.EventType.*
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.EventPresenter
import ru.security.live.presentation.view.iview.EventView
import ru.security.live.presentation.view.ui.adapters.EventImageAdapter
import ru.security.live.util.*
/**
 * @author sardor
 */
class EventActivity : BaseActivity(), EventView, View.OnClickListener {

    @InjectPresenter
    lateinit var presenter: EventPresenter

    @ProvidePresenter
    fun providesPresenter(): EventPresenter {
        return EventPresenter(intent.getStringExtra(EXTRA_EVENT_ID))
    }

    var fromMap = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event)
        setUpHomeToolbar()
        fromMap = intent.extras?.getInt(INTENT_EVENT_FROM_MAP) ?: -1
        tvToolbar.text = getString(R.string.eventDetails)
        mapView.tileProvider.clearTileCache()
        tvSource.setOnClickListener(this)
        tvLocation.setOnClickListener(this)
        tvDate.setOnClickListener(this)
        tvMessage.setOnClickListener(this)
        rvImages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    override fun onClick(v: View?) {
        when (v) {
            tvSource -> handleVisibility(ivArrow1, arrayOf(tvSourceData))
            tvLocation -> handleVisibility(ivArrow2, arrayOf(locationContainer))
            tvDate -> handleVisibility(ivArrow3, arrayOf(tvDateData, btnArchive))
            tvMessage -> handleVisibility(ivArrow4, arrayOf(messageDataContainer))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun showDetails(event: EventDetailed, camera: CameraFull?, archives: List<ArchiveItem>) {

        hideCancellableProgress()
        tvSourceData.text = event.source
        tvLocationData.text = event.location
        tvDateData.text = "${event.getFormattedTime()}"

        var archive: ArchiveItem? = if (archives.isEmpty()) null else archives.first()

//        var archiveAvailable = false
//        var archive: ArchiveItem? = null
//
//        if (archives != null){
//            LoggingTool.log("Archive from ${archives.first().from} to ${archives.first().to}")
//            archive = archives[0]
//
//            for(a in archives){
//                val endAfter = a.getEnd().time >= event.getArchiveEnd()
//                if (a.getStart().time <= event.getArchiveStart()
//                        && endAfter){
//                    archiveAvailable = true
//                    archive = a
//                    break
//                }
//            }
//
//            LoggingTool.log("Archive available $archiveAvailable starts at " +
//                    "${getEventArchiveStart(event.time)} ends at ${getEventArchiveEnd(event.time)}")
//        }

        if (camera != null && (archives.isNotEmpty())) {
            btnArchive.visibility = View.VISIBLE
            btnArchive.setOnClickListener {
                Navigator.navigateToArchiveFromEvent(this, camera, archive!!)
            }
        } else
            btnArchive.isEnabled = false

        when (event.type) {
            UserEvent -> {
                val userEventData = event.userEventData!!
                tvMessageData.text = userEventData.message
                val adapter = EventImageAdapter(
                        userEventData.images ?: listOf(), ::onImageClick)
                rvImages.adapter = adapter
                rvImages.visibility = View.VISIBLE
            }
            EventCount -> {
                tvMessageData.text = event.body
            }
            EventMix -> {
                val mixedEventData = event.mixedEventData!!
                tvMessageData.text = mixedEventData.body
                if (mixedEventData.getDenominationText().isNotEmpty() ||
                        mixedEventData.getAmountText().isNotEmpty() ||
                        mixedEventData.getTotalText().isNotEmpty()) {
                    tvDenomination.text = mixedEventData.getDenominationText()
                    tvAmount.text = mixedEventData.getAmountText()
                    tvTotal.text = mixedEventData.getTotalText()
                    layoutTable.visibility = View.VISIBLE
                }
            }
            GRNRecognized -> {
                val grnData = event.grnData!!
                tvNumber.text = grnData.plateNumber
                tvSpeed.text = grnData.speed.toString()
                tvDirection.text = grnData.direction
                tvMessageData.visibility = View.GONE
                layoutGrn.visibility = View.VISIBLE

                val adapter = EventImageAdapter(grnData.images
                        .filter { !it.isNullOrEmpty() }.map { it!! }, ::onImageClick)
                rvImages.adapter = adapter
                rvImages.visibility = View.VISIBLE
            }
            FaceCaptured -> {
                val faceCapturedData = event.faceCapturedData!!
                tvMessageData.text = faceCapturedData.name

                if (faceCapturedData.getImageList().isNotEmpty()) {
                    val adapter = EventImageAdapter(faceCapturedData.getImageList(), ::onImageClick)
                    rvImages.adapter = adapter
                    rvImages.visibility = View.VISIBLE
                }
            }
            Default -> {
                tvMessageData.text = event.message
            }
        }

        if (event.device?.placementType == 1 || event.device?.placementType == 2) {
            val device = event.device!!

            setUpMap(device)
        }

        if (event.camera != null) {
            btnBroadcast.visibility = View.VISIBLE
            btnBroadcast.setOnClickListener {
                Navigator.navigateToVideo(this, event.camera!!.cameraId, isFromEvent = true)
            }
        }
    }

    private fun onImageClick(link: String) {
        //TODO open fullscreen
    }

    override fun handleVisibility(arrow: ImageView, details: Array<View>) {
        if (details[0].visibility == View.VISIBLE) {
            closeElement(arrow, details)
        } else {
            openElement(arrow, details)
        }
    }

    override fun openElement(arrow: ImageView, details: Array<View>) {
        AnimationHelper.rotate90Left(arrow, object : AnimationEndListener {
            override fun onAnimationEnd(animation: Animation?) {
                details.forEach {
                    if (it.isEnabled) it.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun closeElement(arrow: ImageView, details: Array<View>) {
        AnimationHelper.rotate90Right(arrow, object : AnimationEndListener {
            override fun onAnimationEnd(animation: Animation?) {
                details.forEach {
                    it.visibility = View.GONE
                }
            }
        })
    }

    override fun setUpMap(device: EventDetailed.Device) {

        val scale = baseContext.resources.displayMetrics.density
        if (device.placementType == 1) {
            if (device.tileTemplate == null)
                return
            val url = device.tileTemplate!!
            mapView.setTileSource(EventTileSource(url, scale))
            btnMap.visibility = View.GONE
            mapView.isHorizontalMapRepetitionEnabled = false
            mapView.isVerticalMapRepetitionEnabled = false
        } else {
            mapView.setTileSource(OurTileSource("http://10.178.3.12/osm_tiles/{z}/{x}/{y}.png", scale))
            btnMap.visibility = View.GONE
        }
        if (fromMap == 1)
            btnMap.visibility = View.GONE
        val zoom = if (device.tileTemplate != null) 1.0 else 8.0

        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        mapView.controller.setCenter(GeoPoint(device.lat, device.lon))
        mapView.controller.setZoom(zoom)
        mapView.invalidate()

        val marker = OurMarker(
                point =
                Device(Info(device.deviceType, device.id, device.events, device.name, device.lat, device.lon,
                        null), null, null, null, null,
                        null, true),
                map = mapView)
        mapView.overlays.add(0, marker)

        // mapView.minimumHeight = mapView.width
        mapView.visibility = View.VISIBLE
        btnMap.setOnClickListener {
            Navigator.navigateToMap(
                    this,
                    if (device.tileTemplate != null) Plan(device.tileTemplate!!, "") else null,
                    device.lat,
                    device.lon)
        }
    }
}