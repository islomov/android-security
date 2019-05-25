package ru.security.live.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.EventsRepository
import ru.security.live.domain.entity.EventItem
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.domain.entity.FilterCommon
import ru.security.live.domain.interactor.EventsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.EventsView
import ru.security.live.util.DateTool
import ru.security.live.util.EVENTS_PER_PAGE
import java.util.*
import kotlin.collections.ArrayList
/**
 * @author sardor
 */
@InjectViewState
class EventsPresenter : MvpPresenter<EventsView>() {

    private val interactor = EventsInteractor(EventsRepository)
    val importanceList = arrayListOf(false, false, false)
    var cachedData = HashMap<Int, ArrayList<EventItem>>() // кэшируются только 5 страниц
    var size = 0
    var page = 0

    val groups = ArrayList<String>()
    val types = ArrayList<String>()
    var calendarFrom = DateTool.getDefaultFrom()
    var calendarTo = DateTool.getDefaultTill()
    var checkedLocations = ArrayList<EventLocationItem>()

    //--Здесь рабочие методы
    fun getEventsInitData() {

        val d = interactor.eventsInitData(
                FilterCommon(
                        severity = importanceList
                                .mapIndexed { index, b -> if (b) index else 3 }
                                .filter { it < 3 }
                                .sorted(),
                        take = EVENTS_PER_PAGE,
                        skip = 0,
                        from = DateTool.formatForRequest(calendarFrom),
                        to = DateTool.formatForRequest(calendarTo),
                        unreadOnly = true,
                        eventTypeGroup = groups,
                        eventType = types,
                        location = checkedLocations.filter { it.id.isNotEmpty() }.map { it.id }))

                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            viewState.init(Math.ceil(it.first.toDouble() / EVENTS_PER_PAGE).toInt())
                            viewState.setEventListData(it.second)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

    fun getEventListData() {
        val d = interactor.eventListData(
                FilterCommon(
                        severity = importanceList
                                .mapIndexed { index, b -> if (b) index else 3 }
                                .filter { it < 3 }
                                .sorted(),
                        skip = EVENTS_PER_PAGE * page,
                        take = EVENTS_PER_PAGE,
                        from = DateTool.formatForRequest(calendarFrom),
                        to = DateTool.formatForRequest(calendarTo),
                        eventTypeGroup = groups,
                        eventType = types,
                        location = checkedLocations.map { it.id }))
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            viewState.setEventListData(it)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

    fun updateFilters(from: Calendar, to: Calendar, sources: ArrayList<String>,
                      events: ArrayList<String>, locations: ArrayList<EventLocationItem>) {
        calendarFrom = from
        calendarTo = to
        groups.clear()
        groups.addAll(sources)
        types.clear()
        types.addAll(events)
        checkedLocations.clear()
        checkedLocations.addAll(locations)
    }

    private fun checkActualPageInCache(number: Int): ArrayList<Int> {
        val cachedPages = ArrayList(cachedData.keys)
        val futurePages = when (number) {
            in 0..2 -> arrayListOf(0, 1, 2, 3, 4)
            in (size - 2)..size -> arrayListOf(size - 4, size - 3, size - 2, size - 1, size)
            else -> arrayListOf(number - 2, number - 1, number, number + 1, number + 2)
        }
        cachedData.keys.subtract(futurePages).forEach { cachedData.remove(it) }
        futurePages.removeAll(cachedPages)
        return futurePages
    }
}