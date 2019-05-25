package ru.security.live.presentation.view.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.toolbar.*
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.security.live.R
import ru.security.live.data.pref.ServerPref
import ru.security.live.domain.entity.*
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.MvpMapPresenter
import ru.security.live.presentation.view.iview.MvpMapView
import ru.security.live.presentation.view.ui.dialog.BuildingDialog
import ru.security.live.presentation.view.ui.dialog.ClusterDialog
import ru.security.live.presentation.view.ui.dialog.DeviceDialog
import ru.security.live.util.*
import ru.security.live.util.markers.CustomRadiusMarkerClusterer
import ru.security.live.util.markers.MarkerCoordinatesHelper.Companion.getBoundingBox
/**
 * @author sardor
 */
class MapActivity : BaseActivity(), Marker.OnMarkerClickListener, MvpMapView {

    @InjectPresenter
    lateinit var presenter: MvpMapPresenter

    private val pointList = ArrayList<Point>()
    private var query = ""
    private var isPlan = false

    private var centerLat = 0.0
    private var centerLon = 0.0
    private var markerMe: Marker? = null

    private var minLat = 0.0
    private var maxLat = 0.0
    private var minLon = 0.0
    private var maxLon = 0.0
    private var rezoomed = false

    private val permissions by lazy {
        RxPermissions(this)
    }

    private fun getNewCluster(markers: ArrayList<OurMarker>): CustomRadiusMarkerClusterer {

        val cluster = CustomRadiusMarkerClusterer(this)

        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.pin_grey)
        val bitmap = (drawable as BitmapDrawable).bitmap
        cluster.setIcon(bitmap)
        markers.forEach { cluster.add(it) }
        return cluster
    }

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(
                applicationContext,
                PreferenceManager.getDefaultSharedPreferences(applicationContext)
        )

        setContentView(R.layout.activity_map)
        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.map)
        map.tileProvider.clearTileCache()
        centerLat = intent.getDoubleExtra(INTENT_MAP_LAT, 0.0)
        centerLon = intent.getDoubleExtra(INTENT_MAP_LON, 0.0)

        val plan = intent.getParcelableExtra<Plan>("plan")
        if (plan != null && plan.url.isEmpty()) {
            map.visibility = View.GONE
            groupEmptyView.visibility = View.VISIBLE
            return
        }
        val scale = baseContext.resources.displayMetrics.density
        val tile = if (plan != null) {
            map.minZoomLevel = 1.0
            map.maxZoomLevel = 10.0
            EventTileSource(plan.url, scale)
        } else {
            map.minZoomLevel = 3.0
            map.maxZoomLevel = 15.0
            OurTileSource("${ServerPref.url1}", scale)
        }

        map.setTileSource(tile)

//        map.isHorizontalMapRepetitionEnabled = false
//        map.isVerticalMapRepetitionEnabled = false
        map.setMultiTouchControls(true)
        map.setBuiltInZoomControls(false)
        map.isTilesScaledToDpi = true
        if (plan != null) {
            map.controller.setZoom(1.0)
        } else {
            map.controller.setZoom(3.0)
        }

        map.invalidate()


        isPlan = plan != null

        if (plan != null) {
            isPlan = true
            tvToolbar.text = getString(R.string.objectPlan)
            presenter.getPlaceDevices(plan.id)
        } else {
            isPlan = false
            presenter.getDevices()
        }

        if (!isPlan) {
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
        map.tileProvider.clearTileCache()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (!isPlan) {
            menuInflater.inflate(R.menu.map_menu, menu)

            val searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    query = newText
                    setMarkers()
                    return false
                }
            })
        }

        return true
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
        if (marker != null && marker is OurMarker) {
            dialog(marker.point)
        }
        return true
    }

    private fun dialog(point: Point) {
        when (point) {
            is Building -> {
                val dialog = BuildingDialog()
                dialog.building = point
                dialog.onPlanClick = ::onPlanClick
                dialog.show(supportFragmentManager, "BuildingDialog")
            }
            is Cluster -> {
                val dialog = ClusterDialog()
                dialog.cluster = point
                dialog.onClick = ::onClusterClick
                dialog.show(supportFragmentManager, "BuildingDialog")
            }
            is Device -> {
                val dialog = DeviceDialog()
                dialog.device = point
                dialog.show(supportFragmentManager, "DeviceDialog")
            }
        }
    }

    private fun onPlanClick(planId: String) {
        presenter.getBuildingPlan(planId)
    }

    private fun onClusterClick(point: Point) {
        dialog(point)
    }

    override fun updatePoints(points: Collection<Point>) {
        pointList.clear()
        pointList.addAll(points)
    }

    override fun setMarkers() {
        if (pointList.isNotEmpty()) {
            val markers = ArrayList<OurMarker>()
            minLat = pointList[0].info.lat
            maxLat = pointList[0].info.lat
            maxLon = pointList[0].info.long
            minLon = pointList[0].info.long

            pointList
                    .filter { point ->

                        minLat = Math.min(minLat, point.info.lat)
                        minLon = Math.min(minLon, point.info.long)
                        maxLat = Math.max(maxLat, point.info.lat)
                        maxLon = Math.max(maxLon, point.info.long)

                        point.info.title.contains(query, true) ||
                                (point is Cluster && point.list.any { it.info.title.contains(query, true) })
                    }
                    .forEach { point ->
                        if (point is Cluster) {
                            val cluster = Cluster(point.info, (point as? Cluster)?.list?.filter { it.info.title.contains(query, true) }!!)
                            cluster.list.forEach { _ ->
                                val marker = OurMarker(cluster, map)
                                marker.id = cluster.info.id
                                marker.setOnMarkerClickListener(this)
                                markers.add(marker)
                            }

                        } else {
                            val marker = OurMarker(point, map)
                            marker.id = point.info.id
                            if (marker.point.info.type != null) {
                                marker.setOnMarkerClickListener(this)
                                markers.add(marker)
                            }
                        }
                    }

            //map.setScrollableAreaLimitDouble(BoundingBox(maxLat, maxLon, minLat, minLon).increaseByScale(3f))

            val cluster = getNewCluster(markers)
            cluster.setOnClusterClickListener(this)
            for (o in map.overlays)
                map.overlays.remove(o)
            map.overlays.add(0, cluster)
            map.invalidate()

            map.isTilesScaledToDpi = true

            //map.overlays[0].bounds

            centerLat = (minLat + maxLat) / 2
            centerLon = (minLon + maxLon) / 2

            if (markerMe != null) {
                minLat = Math.min(minLat, markerMe!!.position.latitude)
                minLon = Math.min(minLon, markerMe!!.position.longitude)
                maxLat = Math.max(maxLat, markerMe!!.position.latitude)
                maxLon = Math.max(maxLon, markerMe!!.position.longitude)
                map.overlays.add(0, markerMe)
            }
            if (!rezoomed) {
                val allMarkers = ArrayList<Marker>()
                if (markerMe != null) allMarkers.add(markerMe!!)
                allMarkers.addAll(pointList.map { it.mapToMarker(map) })
                if (allMarkers.size == 1) {
                    map.controller.setCenter(allMarkers[0].position)
                } else {
                    val boundBox = getBoundingBox(allMarkers)
                    map.zoomToBoundingBox(boundBox.increaseByScale(1.4f), true)
                }
                rezoomed = true
            }
        }
    }

    override fun openPlan(plan: Plan) {
        Navigator.navigateToMap(this, plan)
    }

    override fun getLocation() {
        SingleShotLocationProvider.requestSingleUpdate(this) {
            //            val center = GeoPoint(it.latitude.toDouble(), it.longitude.toDouble())
//            map.cameraDistance
//
//            markerMe = Marker(map)
//            markerMe!!.position = center
//            markerMe!!.isDraggable = true
//            markerMe!!.setIcon(resources.getDrawable(R.drawable.icon_pin))
//            markerMe!!.setOnMarkerClickListener { marker, mapView ->
//                false
//            }
        }
    }
}
