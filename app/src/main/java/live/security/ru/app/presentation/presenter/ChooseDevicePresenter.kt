package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.EventsRepository
import ru.security.live.domain.interactor.EventsInteractor
import ru.security.live.presentation.view.iview.ChooseDeviceView
/**
 * @author sardor
 */
@InjectViewState
@SuppressLint("CheckResult")
class ChooseDevicePresenter : MvpPresenter<ChooseDeviceView>() {

    private val interactor = EventsInteractor(EventsRepository)

    fun searchDevice(query: String) {
        interactor.searchDevice(query)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe({
                    if (it.isEmpty()) {
                        viewState.cleanList()
                    } else {
                        viewState.showResult(ArrayList(it))
                    }
                }, {
                    viewState.error(it)
                })
    }

}