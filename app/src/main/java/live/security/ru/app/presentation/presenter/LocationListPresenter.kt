package ru.security.live.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.EventsRepository
import ru.security.live.domain.entity.EventLocationItem
import ru.security.live.domain.entity.LocationItem
import ru.security.live.domain.interactor.EventsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.ChooseLocationView
import java.util.*
/**
 * @author sardor
 */
@InjectViewState
class LocationListPresenter : MvpPresenter<ChooseLocationView>() {

    private val interactor = EventsInteractor(EventsRepository)

    var path = ArrayDeque<LocationItem>()
    var locationData = ArrayList<EventLocationItem>()

    fun getCurrentListData() {
        viewState.setDeviceListData(currentList())
    }

    fun getPath(): String {
        var pathh = ""
        for (s in path) {
            if (pathh.isEmpty()) {
                pathh += s.name
            } else {
                pathh += " > ${s.name}"
            }
        }
        return pathh
    }

    fun currentList() = if (path.isEmpty()) locationData
    else path.last.nested

    fun getLocationListStructure() {
        val d = interactor.locationListStructure()
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            locationData.clear()
                            locationData = it as ArrayList<EventLocationItem>
                            viewState.setDeviceListData(it)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

    fun getMoreLocationList(item: EventLocationItem) {
        val d = interactor.locationMoreListStructure(item.id)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            path.add(LocationItem(item.name, it as ArrayList<EventLocationItem>))
                            viewState.openNext(it)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }
}