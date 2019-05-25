package ru.security.live.presentation.view.ui.activity

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.presentation.view.iview.BaseView
import ru.security.live.util.ui.CancellableProgressDialog
import ru.security.live.util.ui.ProgressDialog

/**
 * @author sardor
 */
abstract class BaseActivity : MvpAppCompatActivity(), BaseView {

    lateinit var arrowDrawable: Drawable
    var hasHomeAction = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrowDrawable = resources.getDrawable(R.drawable.icon_back)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                if (!hasHomeAction)
                    finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private val cancellableProgressDialog by lazy {
        CancellableProgressDialog(this)
    }

    private val progressDialog by lazy {
        ProgressDialog(this)
    }

    override fun error(error: Throwable) {
        // todo: обработка ошибки
        hideCancellableProgress()
        error.printStackTrace()
        Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun showCancellableProgress() {
        cancellableProgressDialog.show()
    }

    override fun hideCancellableProgress() {
        cancellableProgressDialog.dismiss()
    }

    override fun showProgress() {
        progressDialog.show()
    }

    override fun hideProgress() {
        progressDialog.dismiss()
    }

    override fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
    }

    override fun setUpHomeToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(arrowDrawable)
        supportActionBar?.title = ""
    }

    override fun showPositionalProgress() {
    }

    override fun hidePositionalProgress() {
    }


}