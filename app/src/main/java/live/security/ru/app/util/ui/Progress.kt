package ru.security.live.util.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.Window
import ru.security.live.R
import android.view.WindowManager
import android.view.Gravity


/**
 * @author sardor
 */
class Progress(activity: Activity, val x: Int, val y: Int) : AppCompatDialog(activity) {

    var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.small_progress)
        setCancelable(false)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        // set "origin" to top left corner, so to speak
        window.setGravity(Gravity.TOP or Gravity.LEFT)

        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val params = window.getAttributes()
        params.x = x
        params.y = y
        window.attributes = params
    }
}