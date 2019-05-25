package ru.security.live.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.DesktopsRepository
import ru.security.live.domain.interactor.DesktopsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.DesktopsView
/**
 * @author sardor
 */
@InjectViewState
class DesktopsPresenter : MvpPresenter<DesktopsView>() {

    private val interactor = DesktopsInteractor(DesktopsRepository)

    //--Здесь рабочие методы
    fun getDesktopsListData(isArchive: Boolean) {
        val d = interactor.desktopsListData(isArchive)
                .subscribe(
                        {
                            viewState.setDesktopsListData(it.reversed())
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }
}