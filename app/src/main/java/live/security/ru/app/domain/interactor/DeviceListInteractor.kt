package ru.security.live.domain.interactors

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.security.live.domain.repository.IDeviceListRepository
import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType


/**
 * @author sardor
 */
class DeviceListInteractor(private val repository: IDeviceListRepository) {

    //--Здесь рабочие методы


    /*fun deviceListData(): Single<List<DelegateViewType>> {
        return repository.deviceListData()//здесь должны быть сортировка
    } */

    fun getDevListStructure(): Single<Pair<ArrayList<DelegateViewType>, ArrayList<DelegateViewType>>> {
        return repository.getDevListStructure()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}