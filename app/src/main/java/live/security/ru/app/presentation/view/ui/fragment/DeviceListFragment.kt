package ru.security.live.presentation.view.ui.fragment

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import kotlinx.android.synthetic.main.devicelist_fragment.*
import ru.security.live.R
import ru.security.live.domain.entity.DeviceItem
import ru.security.live.domain.entity.FolderItem
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenters.DeviceListPresenter
import ru.security.live.presentation.view.iview.DeviceListView
import ru.security.live.presentation.view.ui.adapters.delegate.FolderDelegate
import ru.security.live.presentation.view.ui.adapters.with_delegate.DEVICE_ITEM
import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType
import ru.security.live.presentation.view.ui.adapters.with_delegate.FOLDER_ITEM
import ru.security.live.presentation.view.ui.adapters.with_delegate.RibbonAdapter
import ru.security.live.presentation.views.ui.adapters.delegate.DeviceItemDelegate
/**
 * @author sardor
 */
class DeviceListFragment : BaseFragment(), DeviceListView {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "point")
    lateinit var presenter: DeviceListPresenter

    private lateinit var adapterDevice: RibbonAdapter

    companion object {
        @JvmStatic
        fun newInstance(): DeviceListFragment {
            val fragment = DeviceListFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.devicelist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context != null)
            rvDevice.layoutManager = LinearLayoutManager(context)

        adapterDevice = RibbonAdapter(
                DeviceItemDelegate(this@DeviceListFragment::clickOnDeviceItem) to DEVICE_ITEM,
                FolderDelegate(this@DeviceListFragment::clickOnFolder) to FOLDER_ITEM)
        rvDevice.addItemDecoration(DividerItemDecoration(rvDevice.context,
                LinearLayoutManager(rvDevice.context).orientation))

        rvDevice.adapter = adapterDevice
        presenter.getCurrentListData()
    }

    private fun clickOnDeviceItem(item: DeviceItem) {
        if (activity != null)
            Navigator.navigateToVideoFromCamera(activity!!, item.id)
    }

    private fun clickOnFolder(item: FolderItem) {
        presenter.path.addLast(item)
        presenter.openInner(item)
    }

    override fun setDeviceListData(data: List<DelegateViewType>) {
        adapterDevice.refresh(data)
    }

    override fun error(error: Throwable) {
        Toast.makeText(activity, error.message, Toast.LENGTH_LONG).show()
    }

    override fun openNext(data: List<DelegateViewType>) {
        //TODO("not need be implemented")
    }

    override fun search(query: String?) {

    }
}