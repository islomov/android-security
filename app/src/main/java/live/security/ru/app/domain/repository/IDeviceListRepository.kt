package ru.security.live.domain.repository

import io.reactivex.Single
import ru.security.live.presentation.view.ui.adapters.with_delegate.DelegateViewType

/**
 * @author sardor
 */
interface IDeviceListRepository {
    //--Здесь рабочие методы


    /*fun deviceListData(): Single<List<DelegateViewType>>*/


    fun getDevListStructure(): Single<Pair<ArrayList<DelegateViewType>,
            ArrayList<DelegateViewType>>>

}