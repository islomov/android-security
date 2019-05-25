package ru.security.live.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.util.MapTileIndex
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import ru.security.live.R
import ru.security.live.domain.entity.Cluster
import ru.security.live.domain.entity.Device
import ru.security.live.domain.entity.Point
/**
 * @author sardor
 */
class OurTileSource(url: String, scale: Float = 1f) : XYTileSource(url, 3,
        20, (256 * scale).toInt(), ".png", arrayOf(url.substring(0, url.indexOfFirst { it == '{' }))) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val url = "$baseUrl${MapTileIndex.getZoom(pMapTileIndex)}/${MapTileIndex.getX(pMapTileIndex)}/${MapTileIndex.getY(pMapTileIndex)}${mImageFilenameEnding}"
        LoggingTool.log("URL: ${url}")
        return url
    }
}

class EventTileSource(val url: String, scale: Float) : XYTileSource(url, 1, 12, (256 * scale).toInt(), ".png", arrayOf(url.substring(0, url.indexOfFirst { it == '{' }))) {
    override fun getTileURLString(pMapTileIndex: Long): String {
        val url = (baseUrl + MapTileIndex.getZoom(pMapTileIndex) + "/" + MapTileIndex.getX(pMapTileIndex) + "_" + MapTileIndex.getY(pMapTileIndex)
                + mImageFilenameEnding)
        return url
    }
}

class OurMarker(val point: Point, map: MapView) : Marker(map) {
    init {

        mPosition = GeoPoint(point.info.lat, point.info.long)
        mTitle = point.info.title

        val iconId = when (point.info.type) {
            TYPE_CODE_CAMERA -> {
                if ((point as Device).broadcastAvailable)
                    R.drawable.icon_map_cam_act
                else
                    R.drawable.icon_map_cam
            }
            TYPE_CODE_SENSOR -> R.drawable.icon_map_sensor
            TYPE_CODE_CONTROLLER -> R.drawable.icon_map_cont
            TYPE_CODE_BUILDING -> R.drawable.icon_map_home
            else -> R.drawable.pin_grey
        }

        mIcon = if (point.info.type == TYPE_CODE_LIST) {
            val size = (point as Cluster).list.size
            drawClusterIcon(BitmapFactory.decodeResource(map.context.resources, R.drawable.pin_grey),
                    map.context, size.toString())
        } else {
            map.context.resources.getDrawable(iconId)
        }
    }

    private fun drawClusterIcon(mClusterIcon: Bitmap, context: Context, text: String): BitmapDrawable {
        val finalIcon = Bitmap.createBitmap(mClusterIcon.width, mClusterIcon.height, mClusterIcon.config)
        val iconCanvas = Canvas(finalIcon)
        iconCanvas.drawBitmap(mClusterIcon, 0f, 0f, null)
        val mTextPaint = Paint()
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = 15 * context.resources.displayMetrics.density
        mTextPaint.isFakeBoldText = true
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.isAntiAlias = true
        val mTextAnchorU = 0.5f
        val mTextAnchorV = 0.5f
        val textHeight = (mTextPaint.descent() + mTextPaint.ascent()).toInt()
        iconCanvas.drawText(text,
                mTextAnchorU * finalIcon.width,
                mTextAnchorV * finalIcon.height - textHeight / 2,
                mTextPaint)
        return BitmapDrawable(context.resources, finalIcon)
    }
}