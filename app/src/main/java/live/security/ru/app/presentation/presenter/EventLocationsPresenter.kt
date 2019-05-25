package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import ru.security.live.data.repository.EventsRepository
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.domain.interactor.EventsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.EquipmentsView

/**
 * @author sardor
 */
@SuppressLint("CheckResult")
class EventLocationsPresenter(val viewState: EquipmentsView) {

    private val interactor = EventsInteractor(EventsRepository)
    var location: EventLocationItem? = null

    fun init(isChild: Boolean) {
        if (isChild) {
            loadChildren()
        } else {
            loadLocations()
        }
    }

    private fun loadLocations() {
        val d = interactor.locationListStructure()
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doAfterTerminate { viewState.hideCancellableProgress() }
                .subscribe({
                    val list = ArrayList<EventLocationItem>()
                    list.addAll(it)
                    viewState.showList(list)
                }, {
                    viewState.error(it)
                })
        App.compositeDisposable.add(d)
    }

    private fun loadChildren() {
        val d = interactor.locationMoreListStructure(location!!.id)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doAfterTerminate { viewState.hideCancellableProgress() }
                .subscribe({
                    val list = ArrayList<EventLocationItem>()
                    list.addAll(it)
                    viewState.showList(list)
                }, {
                    viewState.error(it)
                })
        App.compositeDisposable.add(d)
    }

    fun loadChildren(parentItem: EventLocationItem, check: Boolean) {
        val d = interactor.locationMoreListStructure(parentItem.id)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doAfterTerminate { viewState.hideCancellableProgress() }
                .subscribe({ children ->
                    val list = ArrayList<EventLocationItem>()
                    list.addAll(children)
                    if (check)
                        viewState.checkChildren(list)
                    else
                        viewState.uncheckChildren(list)

                    list.forEach {
                        if (it.hasChildren) {
                            loadChildren(it, check)
                        }
                    }
                }, {
                    viewState.error(it)
                })
        App.compositeDisposable.add(d)
    }

    fun loadParents(list: ArrayList<EventLocationItem>, index: Int) {
        if (list.size == index)
            return
        val d = interactor.locationMoreListStructure(list.get(index).id)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doAfterTerminate { viewState.hideCancellableProgress() }
                .subscribe({ children ->
                    val newList = ArrayList<EventLocationItem>()
                    newList.addAll(children)
                    viewState.parentsLoad(Pair(list[index], newList))
                    var newIndex = index + 1
                    loadParents(list, newIndex)
                }, {
                    viewState.error(it)
                })
        App.compositeDisposable.add(d)
    }
}