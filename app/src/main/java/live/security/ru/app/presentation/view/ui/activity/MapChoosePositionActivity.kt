package ru.security.live.presentation.view.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_choose_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import ru.security.live.R
import ru.security.live.util.SingleShotLocationProvider

/**
 * @author sardor
 */
class MapChoosePositionActivity : BaseActivity(), MapEventsReceiver {

    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        setMarker(p)
        return false
    }

    private lateinit var mMarker: Marker
    private val permissions by lazy {
        RxPermissions(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance()
                .load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        setContentView(R.layout.activity_choose_map)

        val center = GeoPoint(55.5554, 37.431)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)
        mapView.setBuiltInZoomControls(false)
        mapView.controller.setCenter(center)
        mapView.controller.setZoom(8.0)
        mapView.invalidate()

//        --- Create Marker
        mMarker = Marker(mapView)
        mMarker.position = center
        mMarker.isDraggable = true
        mMarker.setIcon(resources.getDrawable(R.drawable.icon_pin))
        mMarker.setOnMarkerClickListener { _, _ ->
            false
        }
        mapView.overlays.add(mMarker)

        tlbChooseCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        tlbChooseApply.setOnClickListener {
            val intent = Intent()
            intent.putExtra("name", tvChangeGeoLatLng.text.toString())
            setResult(Activity.RESULT_OK, intent)
            finish()
        }

        val overlayEvents = MapEventsOverlay(this)
        mapView.overlays.add(overlayEvents)

        permissions.request(Manifest.permission.ACCESS_FINE_LOCATION)
                .subscribe {
                    if (it) {
                        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                            getLocation()
                        else error(Throwable(getString(R.string.errorGPS)))
                    } else {
                        error(Throwable(getString(R.string.noPermission)))
                    }
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

    private fun getLocation() {
        SingleShotLocationProvider.requestSingleUpdate(this) {
            val center = GeoPoint(it.latitude.toDouble(), it.longitude.toDouble())
            mapView.controller.setCenter(center)
            setMarker(center)
        }
    }

    private fun setMarker(geoPoint: GeoPoint?) {
        mMarker.position = geoPoint
        tvChangeGeoLatLng.text = geoPoint.toString()
        mapView.invalidate()
    }
}