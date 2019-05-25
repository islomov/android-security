package ru.security.live.presentation.view.ui.fragment

import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatFragment
import ru.security.live.presentation.view.iview.BaseView
import ru.security.live.util.ui.CancellableProgressDialog
import ru.security.live.util.ui.ProgressDialog
/**
 * @author sardor
 */
abstract class BaseFragment : MvpAppCompatFragment(), BaseView {
    private val cancellableProgressDialog by lazy {
        if (activity != null)
            CancellableProgressDialog(activity!!)
        else null
    }

    private val progressDialog by lazy {
        if (activity != null)
            ProgressDialog(activity!!)
        else null
    }

    override fun showCancellableProgress() {
        cancellableProgressDialog?.show()
    }

    override fun hideCancellableProgress() {
        cancellableProgressDialog?.dismiss()
    }

    override fun showProgress() {
        progressDialog?.show()
    }

    override fun hideProgress() {
        progressDialog?.dismiss()
    }

    override fun error(error: Throwable) {
        error.printStackTrace()
        Toast.makeText(activity, error.message, Toast.LENGTH_SHORT).show()
    }

    override fun setUpToolbar() {

    }

    override fun setUpHomeToolbar() {
    }

    override fun showPositionalProgress() {

    }

    override fun hidePositionalProgress() {

    }
}
