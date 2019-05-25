package ru.security.live.presentation.view.ui.fragment

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.android.synthetic.main.fragment_filter.*
import ru.security.live.R
import ru.security.live.presentation.presenter.FilterEventPresenter
import ru.security.live.presentation.view.iview.FilterEventView
import ru.security.live.presentation.view.ui.activity.EventFilterActivity
import ru.security.live.presentation.view.ui.adapters.FilterEventTypeAdapter
import ru.security.live.util.DateTool
import ru.security.live.util.LoggingTool
import java.util.*
/**
 * @author sardor
 */
class FilterEventFragment : BaseFragment(), FilterEventView, View.OnClickListener {

    lateinit var calendarFrom: Calendar
    lateinit var calendarTo: Calendar
    lateinit var sourceIds: ArrayList<String>
    lateinit var eventIds: ArrayList<String>

    @InjectPresenter
    lateinit var presenter: FilterEventPresenter

    lateinit var parentActivity: EventFilterActivity
    val oneDayMillis = 60 * 60 * 24 * 1000
    var isDefault = true

    @ProvidePresenter
    fun providesPresenter(): FilterEventPresenter {
        return FilterEventPresenter(sourceIds, eventIds)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_filter, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        parentActivity = activity as EventFilterActivity

        calendarFrom = parentActivity.calendarFrom
        calendarTo = parentActivity.calendarTo
        sourceIds = parentActivity.sourceIds
        eventIds = parentActivity.eventIds
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onRangeUpdate()
        cbNow.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                calendarTo = Calendar.getInstance()
                containerTill.visibility = View.GONE
                Log.d("appLogTag", "till ${calendarTo.get(Calendar.DAY_OF_MONTH)}." +
                        "${calendarTo.get(Calendar.MONTH)}")
            } else {
                val date = tvTillDate.text.toString()
                val hour = tvTillTime.text.toString()
                calendarTo = if (date.contains("dd"))
                    DateTool.getDefaultTill()
                else
                    DateTool.getAsCalendar(tvTillDate.text.toString(), tvTillTime.text.toString())
                LoggingTool.log("Filter Fragment calendarTo:${calendarTo.get(Calendar.YEAR)}-${calendarTo.get(Calendar.MONTH)}-${calendarTo.get(Calendar.DAY_OF_MONTH)}")
                containerTill.visibility = View.VISIBLE
            }
        }
        tvFromDate.setOnClickListener(this)
        tvFromTime.setOnClickListener(this)
        tvTillDate.setOnClickListener(this)
        tvTillTime.setOnClickListener(this)
        presenter.getEventGroups()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tvFromDate -> showDatePicker(calendarFrom, true)
            R.id.tvFromTime -> showTimePicker(calendarFrom, true)
            R.id.tvTillDate -> showDatePicker(calendarTo, false)
            R.id.tvTillTime -> showTimePicker(calendarTo, false)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onRangeUpdate() {
        if (calendarFrom.after(calendarTo)) {
            error(Throwable(getString(R.string.fromAfterToError)))
            calendarTo.timeInMillis = calendarFrom.timeInMillis + oneDayMillis
        }

        val defDates =
                calendarFrom.timeInMillis == DateTool.getDefaultFrom().timeInMillis &&
                        calendarTo.timeInMillis == DateTool.getDefaultTill().timeInMillis

        LoggingTool.log("defDates $defDates")

        if (!isDefault || !defDates) {
            tvFromDate.text = DateTool.format(calendarFrom, DateTool.ddMMyyyy)
            tvFromTime.text = DateTool.format(calendarFrom, DateTool.HHmm)
            tvTillDate.text = DateTool.format(calendarTo, DateTool.ddMMyyyy)
            tvTillTime.text = DateTool.format(calendarTo, DateTool.HHmm)
        } else {
            tvFromDate.text = "dd.mm.yyyy"
            tvFromTime.text = "hh:MM"
            tvTillDate.text = "dd.mm.yyyy"
            tvTillTime.text = "hh:MM"
        }
    }

    override fun showSources(adapter: FilterEventTypeAdapter) {
        val manager = LinearLayoutManager(activity)
        rvSources.isNestedScrollingEnabled = false
        rvSources.layoutManager = manager
        rvSources.adapter = adapter
    }

    override fun showEvents(adapter: FilterEventTypeAdapter) {
        val manager = LinearLayoutManager(activity)
        rvEventTypes.isNestedScrollingEnabled = false
        rvEventTypes.layoutManager = manager
        rvEventTypes.adapter = adapter
    }

    override fun showDatePicker(calendar: Calendar, isFrom: Boolean) {
        DatePickerDialog(activity,
                DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    // We need millisecond set to none zero,it needs when we forming filter
//                    calendar.set(Calendar.SECOND, 1)
                    isDefault = false
                    parentActivity.isDef = false
                    onRangeUpdate()
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show()
    }

    override fun showTimePicker(calendar: Calendar, isFrom: Boolean) {
        TimePickerDialog(activity,
                TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    calendar.set(Calendar.MINUTE, minute)
//                    calendar.set(Calendar.SECOND, 1)
                    isDefault = false
                    parentActivity.isDef = false
                    onRangeUpdate()
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true)
                .show()
    }

    override fun runOnMainThread(action: () -> Unit) {
        activity?.runOnUiThread {
            action()
        }
    }

    override fun resetFilters() {
        isDefault = true
        calendarFrom = DateTool.getDefaultFrom()
        calendarTo = DateTool.getDefaultTill()
        cbNow.isChecked = false
        onRangeUpdate()
        presenter.cleanFilters()
    }
}