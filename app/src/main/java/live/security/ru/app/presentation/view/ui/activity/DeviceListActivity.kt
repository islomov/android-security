package ru.security.live.presentation.view.ui.activity

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.DeviceItem
import ru.security.live.domain.entity.FolderItem
import ru.security.live.presentation.presenters.DeviceListPresenter
import ru.security.live.presentation.view.iview.DeviceListView
import ru.security.live.presentation.view.ui.adapters.with_delegate.DEVICE_ITEM
import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType
import ru.security.live.presentation.view.ui.adapters.with_delegate.FOLDER_ITEM
import ru.security.live.presentation.view.ui.fragment.DeviceListFragment
/**
 * @author sardor
 */
class DeviceListActivity : BaseActivity(), DeviceListView {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "point")
    lateinit var presenter: DeviceListPresenter

    private lateinit var fragmentManager: FragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)
        setUpHomeToolbar()
        hasHomeAction = true
        tvToolbar.text = getString(R.string.elements)


        fragmentManager = supportFragmentManager
        fragmentManager.popBackStack()

        presenter.getDevListStructure()
        fragmentManager.beginTransaction()
                .replace(R.id.container, DeviceListFragment.newInstance())
                .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.path.clear()
        presenter.deviceData.clear()
        presenter.allElements.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (presenter.path.isNotEmpty()) presenter.path.removeLast()
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.devices_options, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                search(newText)
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun setDeviceListData(data: List<DelegateViewType>) {

    }

    override fun openNext(data: List<DelegateViewType>) {
        if (presenter.path.isNotEmpty()) {// костыль!!! почему-то при повторном заходе на экран (когда однажды view уже появлялась), происходит clickOnFolder во Fragment, хз почему

            fragmentManager.beginTransaction()
                    .setCustomAnimations(R.animator.slide_from_right, R.animator.slide_in_left)
                    .replace(R.id.container, DeviceListFragment.newInstance())
                    .addToBackStack(null)
                    .commit()
        }
    }

    override fun search(query: String?) {
        val searchedList = presenter.currentList().filter {
            when (it.viewType) {
                DEVICE_ITEM -> (it as DeviceItem).name.contains(query ?: "", true)
                FOLDER_ITEM -> (it as FolderItem).name.contains(query ?: "", true)
                else -> false
            }
        }
        presenter.refreshList(searchedList)
    }
}
