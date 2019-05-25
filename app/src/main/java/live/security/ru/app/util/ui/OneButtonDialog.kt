package ru.security.live.util.ui

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatDialog
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import ru.security.live.R
/**
 * @author sardor
 */
class OneButtonDialog(context: Activity) : AppCompatDialog(context) {
    private var btnSubmit: Button? = null
    private var tvTitle: TextView? = null
    private var tvMessage: TextView? = null

    lateinit var onClick: () -> Unit
    var mTitle = ""
    var mMessage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.one_button_dialog)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnSubmit?.setOnClickListener {
            onClick()
            dismiss()
        }

        tvTitle = findViewById(R.id.tvTitle)
        tvTitle?.text = mTitle
        tvMessage = findViewById(R.id.tvMessage)
        tvMessage?.text = mMessage
    }

    override fun show() {
        super.show()
        this.window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}