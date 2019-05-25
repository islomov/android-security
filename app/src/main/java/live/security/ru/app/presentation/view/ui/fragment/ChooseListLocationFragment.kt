package ru.security.live.presentation.view.ui.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import kotlinx.android.synthetic.main.locationlist_fragment.*
import ru.security.live.R
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.domain.entity.LocationItem
import ru.security.live.presentation.presenter.LocationListPresenter
import ru.security.live.presentation.view.iview.ChooseLocationView
import ru.security.live.presentation.view.ui.adapters.EventLocationAdapter
import java.util.*
/**
 * @author sardor
 */
class ChooseListLocationFragment : BaseFragment(), ChooseLocationView, EventLocationAdapter.OnEventLocationItemClickListener {

    @InjectPresenter(type = PresenterType.GLOBAL, tag = "Location")
    lateinit var presenter: LocationListPresenter

    private lateinit var adapter: EventLocationAdapter

    companion object {
        @JvmStatic
        fun newInstance(): ChooseListLocationFragment {
            val fragment = ChooseListLocationFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.locationlist_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (context != null)
            rvLocation.layoutManager = LinearLayoutManager(context)
        rvLocation.addItemDecoration(DividerItemDecoration(rvLocation.context,
                LinearLayoutManager(rvLocation.context).orientation))
        presenter.getCurrentListData()
    }

    override fun onClick(item: EventLocationItem) {
        if (!item.hasChildren) {
            val intent = Intent()
            presenter.path.add(LocationItem(item.name, ArrayList()))
            intent.putExtra("location", presenter.getPath())
            intent.putExtra("locationId", item.id)
            activity?.setResult(Activity.RESULT_OK, intent)
            activity?.finish()
        } else {
            presenter.getMoreLocationList(item)
        }
    }

    override fun setDeviceListData(data: List<EventLocationItem>) {
        adapter = EventLocationAdapter(data, this,
                object : EventLocationAdapter.OnEventLocationItemClickListener {
                    override fun onClick(item: EventLocationItem) {
                        val intent = Intent()
                        presenter.path.add(LocationItem(item.name, ArrayList()))
                        intent.putExtra("location", presenter.getPath())
                        intent.putExtra("locationId", item.id)
                        activity?.setResult(Activity.RESULT_OK, intent)
                        activity?.finish()
                    }
                })
        rvLocation.adapter = adapter
    }

    override fun error(error: Throwable) {
        Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show()
    }

    override fun openNext(data: List<EventLocationItem>) {}
}