package ru.security.live.presentation.view.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.desktops_activity.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.DesktopsCamItem
import ru.security.live.domain.entity.DesktopsItem
import ru.security.live.presentation.presenter.DesktopsPresenter
import ru.security.live.presentation.view.iview.DesktopsView
import ru.security.live.presentation.view.ui.adapters.DesktopsAdapter
import ru.security.live.util.INTENT_ARCHIVE_CAMERA_URL
import ru.security.live.util.INTENT_DESKTOP_POSITION
import ru.security.live.util.INTENT_FROM_ARCHIVE

/**
 * @author sardor
 */
class DesktopsActivity : BaseActivity(), DesktopsView {
    @InjectPresenter
    lateinit var presenter: DesktopsPresenter

    private lateinit var adapterDesktops: DesktopsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.desktops_activity)
        setUpHomeToolbar()

        rvDesktops.layoutManager = LinearLayoutManager(this)

        adapterDesktops = DesktopsAdapter(this@DesktopsActivity::clickOnDesktopsItem)

        rvDesktops.adapter = adapterDesktops
        val isArchive = intent.getBooleanExtra(INTENT_FROM_ARCHIVE, false)

        tvToolbar.text = getString(if (isArchive) R.string.availableArchives else R.string.availableDesktops)
        presenter.getDesktopsListData(isArchive)
    }

    private fun clickOnDesktopsItem(item: DesktopsItem, position: Int) {
        val intent = Intent()
        when (item) {
            is DesktopsCamItem -> intent.putExtra(INTENT_DESKTOP_POSITION, position)
            is ArchiveItem -> intent.putExtra(INTENT_ARCHIVE_CAMERA_URL, item.url)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun setDesktopsListData(data: List<DesktopsItem>) {
        adapterDesktops.submitList(data)
    }
}