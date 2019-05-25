package ru.security.live.presentation.view.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import com.arellomobile.mvp.presenter.InjectPresenter
import io.reactivex.observers.DisposableObserver
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_events.*
import kotlinx.android.synthetic.main.activity_events.view.*
import ru.security.live.R
import ru.security.live.domain.entity.EventItem
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.presentation.Navigator
import ru.security.live.presentation.presenter.EventsPresenter
import ru.security.live.presentation.view.iview.EventsView
import ru.security.live.presentation.view.ui.adapters.EventAdapter
import ru.security.live.presentation.view.ui.adapters.PageAdapter
import ru.security.live.util.*
import java.util.*
import kotlin.collections.ArrayList
/**
 * @author sardor
 */
class EventsActivity : BaseActivity(), EventsView {

    @InjectPresenter
    lateinit var presenter: EventsPresenter


    private var calendarFrom = DateTool.getDefaultFrom()
    private var calendarTo = DateTool.getDefaultTill()
    var sourceIds: ArrayList<String> = ArrayList()
    var eventIds: ArrayList<String> = ArrayList()
    var checkedLocations = ArrayList<EventLocationItem>()
    var checkedBuildings = ArrayList<EventLocationItem>()
    var isDef = true
    var fromMap: Int = -1
    //    private var currentPage = 0
    private lateinit var adapterEvent: EventAdapter
    private lateinit var adapterPage: PageAdapter
    private val filterSubject: Subject<View> = PublishSubject.create()
    private val filterObserver = object : DisposableObserver<View>() {
        override fun onComplete() { /*TODO("not implemented")*/
        }

        override fun onError(e: Throwable) {
            error(e)
        }

        override fun onNext(view: View) {
            val key = saverity(view.id)
            presenter.importanceList[key] = !(presenter.importanceList[key])
            if (presenter.importanceList.all { it }) {
                for (i in 0..2) presenter.importanceList[i] = false
                ivImportanceHard.isSelected = false
                ivImportanceMiddle.isSelected = false
                ivImportanceSoft.isSelected = false
                presenter.getEventsInitData()
            } else {
                view.isSelected = presenter.importanceList[key]
                //presenter.getEventFilteredListData()
                presenter.getEventsInitData()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        val location = intent.getParcelableExtra<EventLocationItem>(INTENT_FILTER_LOCATION)
        fromMap = intent.extras?.getInt(INTENT_EVENT_FROM_MAP) ?: -1
        if (location != null) {
            toolbar.toolMenu.setImageResource(R.drawable.hp_filters_applied)
            if (location.type == TYPE_CODE_BUILDING)
                checkedBuildings.add(location)
            else
                checkedLocations.add(location)
            presenter.updateFilters(calendarFrom, calendarTo, sourceIds, eventIds, arrayListOf(location))
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
        toolbar.toolMenu.setOnClickListener {
            val intent = Intent(this, EventFilterActivity::class.java)

            intent.putExtra(INTENT_FILTER_FROM, calendarFrom)
            intent.putExtra(INTENT_FILTER_TILL, calendarTo)
            intent.putExtra(INTENT_IS_DEFAULT, isDef)
            intent.putExtra(INTENT_EVENT_FROM_MAP, fromMap)
            intent.putStringArrayListExtra(INTENT_FILTER_SOURCES, sourceIds)
            intent.putStringArrayListExtra(INTENT_FILTER_EVENT_TYPES, eventIds)
            intent.putParcelableArrayListExtra(INTENT_FILTER_LOCATIONS, checkedLocations)
            intent.putParcelableArrayListExtra(INTENT_FILTER_BUILDINGS, checkedBuildings)

            startActivityForResult(intent, REQUEST_CODE_FILTER)
        }


        ivImportanceHard.setOnClickListener { filterSubject.onNext(it) }
        ivImportanceMiddle.setOnClickListener { filterSubject.onNext(it) }
        ivImportanceSoft.setOnClickListener { filterSubject.onNext(it) }

        rvEvents.layoutManager = LinearLayoutManager(applicationContext)

        adapterEvent = EventAdapter(this@EventsActivity::clickOnEventItem)
        rvEvents.setHasFixedSize(true)
        rvEvents.addItemDecoration(DividerItemDecoration(rvEvents.context,
                LinearLayoutManager(rvEvents.context).orientation))

        filterSubject.subscribe(filterObserver)
        rvEvents.adapter = adapterEvent
        presenter.getEventsInitData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILTER && resultCode == Activity.RESULT_OK) {
            calendarFrom = data!!.getSerializableExtra(INTENT_FILTER_FROM) as Calendar
            calendarTo = data.getSerializableExtra(INTENT_FILTER_TILL) as Calendar
            sourceIds = data.getStringArrayListExtra(INTENT_FILTER_SOURCES)
            eventIds = data.getStringArrayListExtra(INTENT_FILTER_EVENT_TYPES)
            checkedLocations = data.getParcelableArrayListExtra<EventLocationItem>(INTENT_FILTER_LOCATIONS) as ArrayList<EventLocationItem>
            checkedBuildings = data.getParcelableArrayListExtra<EventLocationItem>(INTENT_FILTER_LOCATIONS) as ArrayList<EventLocationItem>
            isDef = calendarFrom.timeInMillis == DateTool.getDefaultFrom().timeInMillis &&
                    calendarTo.timeInMillis == DateTool.getDefaultTill().timeInMillis
            Log.d("appLogTag", "isDef $isDef groups ${sourceIds.size} events ${eventIds.size}")

            val showIndicator = checkedLocations.isNotEmpty() || checkedBuildings.isNotEmpty() || sourceIds.isNotEmpty()
                    || eventIds.isNotEmpty() || !isDef

            toolbar.toolMenu.setImageResource(
                    if (showIndicator) R.drawable.hp_filters_applied else R.drawable.flters)

            presenter.updateFilters(calendarFrom, calendarTo, sourceIds, eventIds, checkedLocations)
            presenter.getEventsInitData()
        }
    }

    private fun clickOnEventItem(item: EventItem) {
        Navigator.navigateToEvent(item.id, this, fromMap)
    }

    private fun clickOnPage(page: Int) {
        presenter.page = page
        presenter.getEventListData()
    }

    override fun init(size: Int) {
        rvPagination.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterPage = PageAdapter(this@EventsActivity::clickOnPage, size)
        rvPagination.setHasFixedSize(true)
        rvPagination.adapter = adapterPage
    }

    override fun setEventListData(data: List<EventItem>) {
        adapterEvent.submitList(data)
        checkEmptyEventList(data.isEmpty())
    }

    private fun checkEmptyEventList(isEmpty: Boolean) {
        if (isEmpty) {
            rvEvents.visibility = View.INVISIBLE
            groupEmptyView.visibility = ConstraintLayout.VISIBLE
            groupEmptyView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.scale))
        } else {
            rvEvents.visibility = View.VISIBLE
            groupEmptyView.visibility = ConstraintLayout.GONE
        }
    }

    override fun onDestroy() {
        filterObserver.dispose()
        super.onDestroy()
    }

    private fun saverity(viewId: Int): Int =
            when (viewId) {
                ivImportanceHard.id -> 2
                ivImportanceMiddle.id -> 1
                else -> 0
            }
}
