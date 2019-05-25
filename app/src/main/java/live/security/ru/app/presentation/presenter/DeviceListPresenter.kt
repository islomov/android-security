package ru.security.live.presentation.presenters

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.DeviceListRepository
import ru.security.live.domain.entity.FolderItem
import ru.security.live.domain.interactors.DeviceListInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.DeviceListView
import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType
import java.util.*
/**
 * @author sardor
 */
@InjectViewState
class DeviceListPresenter : MvpPresenter<DeviceListView>() {

    private val interactor = DeviceListInteractor(DeviceListRepository)
    val path = ArrayDeque<FolderItem>() //Для отслеживания пути
    val deviceData = ArrayList<DelegateViewType>() //Начальный список
    val allElements = ArrayList<DelegateViewType>() //Для поиска среди всех элементов


    //--Здесь рабочие методы
    /*fun getDeviceListData() {
        interactor.deviceListData()
                .subscribe(
                        {
                            viewState.setDeviceListData(it)
                            deviceData = ArrayList(it)
                        },
                        {
                            viewState.error(it)
                        }
                )
    }*/

    fun getCurrentListData() {
        viewState.setDeviceListData(currentList())
    }

    fun currentList() = if (path.isEmpty()) deviceData
    else path.last.nested

    // Пока используется только для поиска
    fun refreshList(data: List<DelegateViewType>) {
        viewState.setDeviceListData(data)
    }

    fun openInner(rootItem: FolderItem) {
        viewState.openNext(rootItem.nested)
    }

    //Для получения данных
    fun getDevListStructure() {
        val d = interactor.getDevListStructure()
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            deviceData.clear()
                            allElements.clear()
                            deviceData.addAll(it.first)
                            allElements.addAll(it.second)
                            viewState.setDeviceListData(deviceData)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }
}