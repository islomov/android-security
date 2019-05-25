package ru.security.live.presentation.presenter

import android.os.Handler
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.EventsRepository
import ru.security.live.domain.entity.FilterEventTypeItem
import ru.security.live.domain.interactor.EventsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.FilterEventView
import ru.security.live.presentation.view.ui.adapters.FilterEventTypeAdapter
import java.util.*
/**
 * @author sardor
 */
@InjectViewState
class FilterEventPresenter(
        private val groupIds: ArrayList<String>,
        private val eventIds: ArrayList<String>
) : MvpPresenter<FilterEventView>() {

    private val interactor = EventsInteractor(EventsRepository)

    private val sourceList = ArrayList<FilterEventTypeItem>()
    private val eventList = ArrayList<FilterEventTypeItem>()

    private lateinit var sourceAdapter: FilterEventTypeAdapter
    private lateinit var eventsAdapter: FilterEventTypeAdapter

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        sourceAdapter = FilterEventTypeAdapter(sourceList, ::onSourceClick)
        eventsAdapter = FilterEventTypeAdapter(eventList, ::onEventClick)
    }

    fun getEventGroups() {
        val d = interactor.getEventSources()
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doAfterTerminate { viewState.hideCancellableProgress() }
                .subscribe({
                    var hasChecked = false
                    val tempList = ArrayList<FilterEventTypeItem>()
                    tempList.addAll(it)
                    tempList.forEach {
                        if (groupIds.contains(it.id)) {
                            it.isChecked = true
                            hasChecked = true
                        }
                    }

                    sourceList.clear()
                    sourceList.addAll(tempList)
                    viewState.showSources(sourceAdapter)
                    viewState.showEvents(eventsAdapter)

                    if (hasChecked) {
                        sourceList.filter { it.isChecked }.forEach {
                            filterEvents(it)
                        }
                    }
                }, {
                    viewState.error(it)
                })
        App.compositeDisposable.add(d)
    }

    private fun filterEvents(source: FilterEventTypeItem) {
        if (!source.isChecked) {
            eventList.removeAll(eventList.filter { it.parentId == source.id })
            eventIds.removeAll(eventList.filter { it.parentId == source.id }.map { it.id })
            eventsAdapter.notifyDataSetChanged()
        } else {
            val d = interactor.getEventTypes(arrayListOf(source))
                    .doOnSubscribe { viewState.showCancellableProgress() }
                    .doAfterTerminate { viewState.hideCancellableProgress() }
                    .subscribe({
                        it.forEach {
                            it.parentId = source.id
                            if (eventIds.contains(it.id))
                                it.isChecked = true
                        }
                        eventList.addAll(it)
                        eventsAdapter.notifyDataSetChanged()
                    }, {
                        viewState.error(it)
                    })
            App.compositeDisposable.add(d)
        }
    }

    private fun onSourceClick(position: Int) {
        val item = sourceList[position]
        item.toggle()
        if (item.isChecked) groupIds.add(item.id)
        else groupIds.remove(item.id)

        try {
            sourceAdapter.notifyItemChanged(position)
        } catch (e: IllegalStateException) {
            Handler().postDelayed({
                sourceAdapter.notifyItemChanged(position)
            }, 50)
        }
        eventsAdapter.notifyItemChanged(position)
        filterEvents(item)
    }

    private fun onEventClick(position: Int) {
        val item = eventList[position]
        item.toggle()
        if (item.isChecked) eventIds.add(item.id)
        else eventIds.remove(item.id)

        try {
            eventsAdapter.notifyItemChanged(position)
        } catch (e: IllegalStateException) {
            Handler().postDelayed({
                eventsAdapter.notifyItemChanged(position)
            }, 50)
        }
    }

    fun cleanFilters() {
        groupIds.clear()
        eventIds.clear()
        for (src in sourceList) {
            src.isChecked = false
        }
        sourceAdapter.notifyDataSetChanged()

        eventList.clear()
        eventsAdapter.notifyDataSetChanged()
    }
}