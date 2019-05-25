package ru.security.live.util.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.Window
import ru.security.live.R

/**
 * Created by Gor on 20.03.2018.
 */
class ProgressDialog(activity: Activity) : AppCompatDialog(activity) {

    var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.progress)
        setCancelable(false)
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}