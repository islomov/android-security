package ru.security.live.util.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
/**
 * @author sardor
 */
class IconGenerator {
    companion object {
        fun drawClusterIcon(mClusterIcon: Bitmap, context: Context, text: String): BitmapDrawable {
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
}