package ru.security.live.util.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.Window
import kotlinx.android.synthetic.main.cancellable_progress.*
import ru.security.live.R
import ru.security.live.presentation.App
/**
 * @author sardor
 */
class CancellableProgressDialog(activity: Activity) : AppCompatDialog(activity) {

    var message = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.cancellable_progress)
        setCancelable(false)
        btnCancel.setOnClickListener {
            App.compositeDisposable.clear()
            dismiss()
        }
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}