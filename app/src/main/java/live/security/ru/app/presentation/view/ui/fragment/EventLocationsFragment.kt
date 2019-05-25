package ru.security.live.presentation.view.ui.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_equipments.*
import ru.security.live.R
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.presentation.presenter.EventLocationsPresenter
import ru.security.live.presentation.view.iview.EquipmentsView
import ru.security.live.presentation.view.ui.activity.EventFilterActivity
import ru.security.live.presentation.view.ui.adapters.FilterEventLocationAdapter
import ru.security.live.util.*
/**
 * @author sardor
 */
class EventLocationsFragment : BaseFragment(), EquipmentsView {

    lateinit var presenter: EventLocationsPresenter

    private var rootView: View? = null
    private var parentFragment: EventLocationsFragment? = null
    private lateinit var parentActivity: EventFilterActivity
    var isChild = false
    var parent: EventLocationItem? = null
    lateinit var adapter: FilterEventLocationAdapter
    val list = ArrayList<EventLocationItem>()
    var checkedLocations = ArrayList<EventLocationItem>()
    var fromMap: Int = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentActivity = activity as EventFilterActivity
        presenter = EventLocationsPresenter(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_equipments, container, false)

        presenter.location = parent
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isChild) {
            tvBack.text = parent?.name
            layoutBack.visibility = View.VISIBLE
            layoutBack.setOnClickListener {
                updateFilters()
                parentActivity.supportFragmentManager.popBackStackImmediate()
            }
        }

        if (list.isEmpty())
            presenter.init(isChild)
        else
            rvLocations.adapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        parentActivity.locationsFragment = this
    }

    override fun showList(array: ArrayList<EventLocationItem>) {
        list.addAll(array)
        val newList = ArrayList(array)
        newList.forEach { location ->
            var old = parentActivity.checkedBuildings.find { it.id == location.id }
            if (old != null)

                location.status = if (parentActivity.checkedLocations.isEmpty()) allChecked else someChecked
        }

        newList.forEach { location ->

            val old = parentActivity.checkedLocations.find { it.parentId == location.id }
            if (old != null)
                location.status = someChecked
        }

        newList.forEach { location ->
            var old: EventLocationItem? = null
            for (item in parentActivity.checkedLocations) {
                if (location.id == item.id && item.parentId == location.parentId) {
                    old = item
                    old?.status = allChecked
                }

            }
            if (old != null)
                location.status = old?.status!!
        }

        adapter = FilterEventLocationAdapter(newList, ::onClick, ::onCheckClick)
        rvLocations.layoutManager = LinearLayoutManager(activity)
        rvLocations.adapter = adapter
        if (!isChild) {
            if (fromMap == 1)
                presenter.loadParents(list, 0)
        }
    }

    override fun openChild(item: EventLocationItem) {
        val fragment = EventLocationsFragment()
        fragment.isChild = true
        fragment.parent = item
        fragment.parentFragment = this
        parentActivity.showLocationFragment(fragment)
    }

    override fun resetFilter() {
        list.forEach {
            it.status = noneChecked
        }

        LoggingTool.log("RV ${rvLocations == null}")

        list.forEach {
            LoggingTool.log("${it.name} ${it.status}")
        }
        parentActivity.checkedBuildings.clear()
        parentActivity.checkedLocations.clear()
        if (rvLocations.adapter != null)
            rvLocations.adapter.notifyDataSetChanged()
    }

    override fun updateFilters() {
        val checked = list.filter { it.status == allChecked }

        if (checked.isNotEmpty() && parent?.status != allChecked)
            parent?.status = someChecked

        if (checked.isEmpty() && parent?.status != allChecked)
            parent?.status = noneChecked

        if (parent?.status != noneChecked)
            parentActivity.checkedBuildings.add(parent!!)
        else
            parentActivity.checkedBuildings.remove(
                    parentActivity.checkedBuildings.find { it.id == parent?.id }
            )
    }

    private fun onClick(position: Int) {
        val item = list[position]
        if (item.hasChildren) {
            openChild(item)
        } else {
            item.toggle()
            if (item.status == allChecked)
                parentActivity.checkedLocations.add(item)
            else
                parentActivity.checkedLocations.remove(item)

            adapter.notifyItemChanged(position)
        }
    }

    private fun onCheckClick(position: Int) {
        val item = list[position]
        item.toggle()
        if (item.status == allChecked)
            parentActivity.checkedLocations.add(item)
        else
            parentActivity.checkedLocations.remove(item)
        adapter.notifyItemChanged(position)
    }

    override fun checkChildren(array: ArrayList<EventLocationItem>) {
        array.forEach { item ->
            item.status = allChecked
            val location = parentActivity.checkedLocations.find { it.id == item.id }
            parentActivity.checkedLocations.remove(location)

            if (item.hasChildren)
                parentActivity.checkedBuildings.add(item)
        }
        parentActivity.checkedLocations.addAll(array)
    }

    override fun uncheckChildren(array: ArrayList<EventLocationItem>) {
        array.forEach { item ->
            item.status = noneChecked
            val location = parentActivity.checkedLocations.find { it.id == item.id }
            parentActivity.checkedLocations.remove(location)
        }
    }

    override fun parentsLoad(pair: Pair<EventLocationItem, ArrayList<EventLocationItem>>) {
        worker.execute {
            val updating = pair.first
            val newList = pair.second
            newList.forEach { location ->
                var item = parentActivity.checkedBuildings.find { it.id == location.id }
                if (item == null) {
                    item = parentActivity.checkedLocations.find { it.parentId == location.id }
                }
                if (item != null) {
                    val index = list.indexOf(updating)
                    if (index != -1) {
                        Handler(Looper.getMainLooper()).post {
                            val newData = updating.copy()
                            newData.status = someChecked
                            adapter.update(index, newData)
                        }
                    }
                }
            }
        }
    }

}