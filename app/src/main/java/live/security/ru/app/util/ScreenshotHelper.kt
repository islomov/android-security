package ru.security.live.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.opengl.GLException
import android.provider.MediaStore
import android.view.View
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.IntBuffer
import javax.microedition.khronos.opengles.GL10
/**
 * @author sardor
 */

class ScreenshotHelper {
    companion object {
        fun takeScreenshotBitmap(view: View): Bitmap {
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createScaledBitmap(view.drawingCache, view.width / 5, view.height / 5, true)
            view.isDrawingCacheEnabled = false

            return bitmap
        }

        fun takeScreenshotFile(view: View): File {
            view.isDrawingCacheEnabled = true
            val bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false

            val file = File("path")
            val outStream = BufferedOutputStream(FileOutputStream(file))
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
            return file
        }

        @Throws(OutOfMemoryError::class)
        fun createBitmapFromGLSurface(x: Int, y: Int, w: Int, h: Int, gl: GL10): Bitmap? {
            val bitmapBuffer = IntArray(w * h)
            val bitmapSource = IntArray(w * h)
            val intBuffer = IntBuffer.wrap(bitmapBuffer)
            intBuffer.position(0)

            try {
                gl.glReadPixels(x, y, w, h, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, intBuffer)
                var offset1: Int
                var offset2: Int
                for (i in 0 until h) {
                    offset1 = i * w
                    offset2 = (h - i - 1) * w
                    for (j in 0 until w) {
                        val texturePixel = bitmapBuffer[offset1 + j]
                        val blue = texturePixel shr 16 and 0xff
                        val red = texturePixel shl 16 and 0x00ff0000
                        val pixel = texturePixel and -0xff0100 or red or blue
                        bitmapSource[offset2 + j] = pixel
                    }
                }
            } catch (e: GLException) {
                return null
            }

            return Bitmap.createBitmap(bitmapSource, w, h, Bitmap.Config.ARGB_8888)
        }

        fun convertToFile(context: Context, bitmap: Bitmap): File? {
            val uri = getImageUri(context, bitmap)
            if (uri != null)
                return File(ImagePicker.getPath(context, uri))
            else return null
        }

        private fun getImageUri(context: Context, inImage: Bitmap): Uri? {
            val bytes = ByteArrayOutputStream()
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path: String? = MediaStore.Images.Media.insertImage(context.contentResolver, inImage, "Title.jpg", "Screenshot")
            if (path != null) {
                return Uri.parse(path)
            } else {
                return null
            }
        }
    }
}