package ru.security.live.presentation.view.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import kotlinx.android.synthetic.main.activity_event_filter.*
import kotlinx.android.synthetic.main.toolbar.*
import ru.security.live.R
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.presentation.view.ui.fragment.EventLocationsFragment
import ru.security.live.presentation.view.ui.fragment.FilterEventFragment
import ru.security.live.util.*
import java.util.*

/**
 * @author sardor
 */
class EventFilterActivity : BaseActivity() {

    var calendarFrom = Calendar.getInstance()
    var calendarTo = Calendar.getInstance()
    lateinit var sourceIds: ArrayList<String>
    lateinit var eventIds: ArrayList<String>

    var checkedLocations = ArrayList<EventLocationItem>()
    var checkedBuildings = ArrayList<EventLocationItem>()

    lateinit var filterFragment: FilterEventFragment
    lateinit var locationsFragment: EventLocationsFragment
    private var currentFragment = Any()
    var isDef = true
    var fromMap: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_filter)
        setUpHomeToolbar()
        tvToolbar.text = getString(R.string.eventsFilter)

        isDef = intent.getBooleanExtra(INTENT_IS_DEFAULT, true)
        calendarFrom = intent.getSerializableExtra(INTENT_FILTER_FROM) as Calendar
        calendarTo = intent.getSerializableExtra(INTENT_FILTER_TILL) as Calendar
        fromMap = intent.extras?.getInt(INTENT_EVENT_FROM_MAP) ?: -1

        LoggingTool.log("Event Filter calendarTo:${calendarTo.get(Calendar.YEAR)}-${calendarTo.get(Calendar.MONTH)}-${calendarTo.get(Calendar.DAY_OF_MONTH)}")
        sourceIds = intent.getStringArrayListExtra(INTENT_FILTER_SOURCES)
        eventIds = intent.getStringArrayListExtra(INTENT_FILTER_EVENT_TYPES)
        checkedLocations = intent.getParcelableArrayListExtra<EventLocationItem>(INTENT_FILTER_LOCATIONS)
                as ArrayList<EventLocationItem>
        checkedBuildings = intent.getParcelableArrayListExtra<EventLocationItem>(INTENT_FILTER_BUILDINGS)
                as ArrayList<EventLocationItem>

        filterFragment = FilterEventFragment()
        filterFragment.isDefault = isDef
        filterFragment.calendarFrom = calendarFrom
        filterFragment.calendarTo = calendarTo
        filterFragment.sourceIds = sourceIds
        filterFragment.eventIds = eventIds

        locationsFragment = EventLocationsFragment()
        locationsFragment.isChild = false
        locationsFragment.checkedLocations = checkedLocations
        locationsFragment.fromMap = fromMap

        showFilterFragment(filterFragment)
        showLocationFragment(locationsFragment)

        currentFragment = filterFragment

        tvFilters.setOnClickListener {
            tvFilters.setTextColor(resources.getColor(R.color.white))
            tvFilters.setBackgroundResource(R.drawable.segment_act)
            tvEquipment.setTextColor(resources.getColor(R.color.lightGray))
            tvEquipment.setBackgroundResource(0)
            tvEquipment.text = getString(R.string.equipment)
            frameContainerFilter.visibility = View.VISIBLE
            frameContainerEquipments.visibility = View.INVISIBLE
            currentFragment = filterFragment
        }
        tvEquipment.setOnClickListener {
            tvEquipment.setTextColor(resources.getColor(R.color.white))
            tvEquipment.setBackgroundResource(R.drawable.segment_act_2)
            tvFilters.setTextColor(resources.getColor(R.color.lightGray))
            tvFilters.setBackgroundResource(0)
            frameContainerFilter.visibility = View.INVISIBLE
            frameContainerEquipments.visibility = View.VISIBLE
            currentFragment = locationsFragment
        }
        btnApply.setOnClickListener {
            calendarFrom = filterFragment.calendarFrom
            calendarTo = filterFragment.calendarTo
            sourceIds = filterFragment.sourceIds
            eventIds = filterFragment.eventIds

            LoggingTool.log("Is Def $isDef")
            val intent = Intent()
            intent.putExtra(INTENT_IS_DEFAULT, isDef)
            intent.putExtra(INTENT_FILTER_FROM, calendarFrom)
            intent.putExtra(INTENT_FILTER_TILL, calendarTo)
            intent.putStringArrayListExtra(INTENT_FILTER_SOURCES, sourceIds)
            intent.putStringArrayListExtra(INTENT_FILTER_EVENT_TYPES, eventIds)
            intent.putParcelableArrayListExtra(INTENT_FILTER_LOCATIONS, checkedLocations)
            intent.putParcelableArrayListExtra(INTENT_FILTER_BUILDINGS, checkedBuildings)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(INTENT_FILTER_FROM, calendarFrom)
        outState.putSerializable(INTENT_FILTER_TILL, calendarTo)
        outState.putStringArrayList(INTENT_FILTER_SOURCES, sourceIds)
        outState.putStringArrayList(INTENT_FILTER_EVENT_TYPES, eventIds)
        outState.putParcelableArrayList(INTENT_FILTER_LOCATIONS, checkedLocations)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.filter_options, menu)
        menu!!.findItem(R.id.reset).actionView.setOnClickListener {
            showCancellableProgress()
            filterFragment.resetFilters()
            locationsFragment.resetFilter()
            isDef = true
            hideCancellableProgress()
            btnApply.performClick()
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun showLocationFragment(fragment: EventLocationsFragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainerEquipments, fragment)
                .addToBackStack("location")
                .commit()
    }

    fun showFilterFragment(fragment: FilterEventFragment) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.frameContainerFilter, fragment)
                .commit()
    }

    override fun onBackPressed() {
        finish()
    }
}
