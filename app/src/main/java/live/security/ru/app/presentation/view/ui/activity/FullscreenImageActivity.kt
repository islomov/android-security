package ru.security.live.presentation.view.ui.activity

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_fullscreen_image.*
import ru.security.live.R

/**
 * @author sardor
 */
class FullscreenImageActivity : AppCompatActivity() {

    companion object {
        lateinit var imageBitmap: Bitmap
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_image)

        ivFullscreen.setImageBitmap(imageBitmap)
    }
}
