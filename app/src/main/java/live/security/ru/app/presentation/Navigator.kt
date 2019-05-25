package ru.security.live.presentation

import android.app.Activity
import android.content.Intent
import com.jakewharton.processphoenix.ProcessPhoenix
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.domain.entity.Plan
import ru.security.live.presentation.view.ui.activity.*
import ru.security.live.util.*
/**
 * @author sardor
 */
object Navigator {
    fun navigateToMenu(activity: Activity) {
        val intent = Intent(activity, MenuActivity::class.java)
        activity.startActivity(intent)
    }

    fun navigateToMain(activity: Activity) {
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
    }

    fun navigateToAuth(activity: Activity) {
        ProcessPhoenix.triggerRebirth(activity)
//        val intent = Intent(activity, AuthActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//        activity.startActivity(intent)
    }

    fun navigateToMap(activity: Activity, plan: Plan? = null) {
        val intent = Intent(activity, MapActivity::class.java)
        if (plan != null) {
            intent.putExtra("plan", plan)
        }
        activity.startActivity(intent)
    }

    fun navigateToMap(activity: Activity, plan: Plan? = null, lat: Double, lon: Double) {
        val intent = Intent(activity, MapActivity::class.java)
        if (plan != null) {
            intent.putExtra("plan", plan)
        }
        intent.putExtra(INTENT_MAP_LAT, lat)
        intent.putExtra(INTENT_MAP_LON, lon)
        activity.startActivity(intent)
    }

    fun navigateToAddEvent(activity: Activity, params: AddEventActivity.Params? = null) {
        val intent = Intent(activity, AddEventActivity::class.java)
        intent.putExtra(INTENT_EVENT_PARAMS, params)
        activity.startActivity(intent)
    }

    fun navigateToVideo(activity: Activity) {
        val intent = Intent(activity, VideoActivity::class.java)
        intent.putExtra(INTENT_VIDEO_TYPE, VideoActivity.typeDesktop)
        activity.startActivity(intent)
    }

    fun navigateToVideo(activity: Activity, deviceId: String, isFromEvent: Boolean = false, isFromMap: Boolean = false) {
        val intent = Intent(activity, VideoActivity::class.java)
        intent.putExtra(INTENT_VIDEO_TYPE, VideoActivity.typeCamera)
        intent.putExtra(INTENT_CAMERA_INFO, deviceId)
        intent.putExtra(INTENT_FROM_EVENT, isFromEvent)
        intent.putExtra(INTENT_VIDEO_FROM_MAP, isFromMap)
        activity.startActivity(intent)
    }

    fun navigateToArchiveFromDeviceDialog(activity: Activity, id: String) {
        val intent = Intent(activity, ArchiveActivity::class.java)
        intent.putExtra(EXTRA_EVENT_ID, id)
        activity.startActivity(intent)
    }

    fun navigateToVideoFromCamera(activity: Activity, deviceId: String) {
        val intent = Intent(activity, VideoActivity::class.java)
        intent.putExtra(INTENT_VIDEO_TYPE, VideoActivity.typeCamera)
        intent.putExtra(INTENT_CAMERA_INFO, deviceId)
        intent.putExtra(INTENT_FROM_CAMERA, true)
        activity.startActivity(intent)
    }

    fun navigateToEvents(activity: Activity) {
        val intent = Intent(activity, EventsActivity::class.java)
        activity.startActivity(intent)
    }

    fun navigateToEvents(activity: Activity, location: EventLocationItem, fromMap: Int = -1) {
        val intent = Intent(activity, EventsActivity::class.java)
        intent.putExtra(INTENT_FILTER_LOCATION, location)
        intent.putExtra(INTENT_EVENT_FROM_MAP, fromMap)
        activity.startActivity(intent)
    }

    fun navigateToDevices(activity: Activity) {
        val intent = Intent(activity, DeviceListActivity::class.java)
        activity.startActivity(intent)
    }

    fun navigateToDesktops(activity: Activity, fromArchive: Boolean = false) {
        val intent = Intent(activity, DesktopsActivity::class.java)
        intent.putExtra(INTENT_FROM_ARCHIVE, fromArchive)
        activity.startActivityForResult(intent, 0)
    }

    fun navigateToArchive(activity: Activity, camera: CameraFull, item: ArchiveItem) {
        val intent = Intent(activity, ArchiveActivity::class.java)
        intent.putExtra(INTENT_CAMERA_INFO, camera)
        intent.putExtra(INTENT_ARCHIVE_INFO, item)
        activity.startActivity(intent)
    }

    fun navigateToArchiveFromEvent(activity: Activity, camera: CameraFull, item: ArchiveItem) {
        val intent = Intent(activity, ArchiveActivity::class.java)
        intent.putExtra(INTENT_CAMERA_INFO, camera)
        intent.putExtra(INTENT_ARCHIVE_INFO, item)
        intent.putExtra(INTENT_ARCHIVE_IS_EVENT, true)
        activity.startActivity(intent)
    }

    fun navigateToEvent(id: String, activity: Activity, fromMap: Int = -1) {
        val intent = Intent(activity, EventActivity::class.java)
        intent.putExtra(EXTRA_EVENT_ID, id)
        intent.putExtra(INTENT_EVENT_FROM_MAP, fromMap)
        activity.startActivity(intent)
    }

    fun navigateToFullscreenVideo(rtmp: String, activity: Activity) {
        val intent = Intent(activity, FullscreenVideoActivity::class.java)
        intent.putExtra(INTENT_RTMP, rtmp)
        activity.startActivity(intent)
    }
}