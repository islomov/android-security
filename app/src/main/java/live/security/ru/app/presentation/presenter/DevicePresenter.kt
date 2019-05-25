package ru.security.live.presentation.presenter

import ru.security.live.data.repository.DesktopsRepository
import ru.security.live.domain.interactor.DesktopsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.DeviceView
/**
 * @author sardor
 */
class DevicePresenter(private val viewState: DeviceView) {

    private val desktopsInteractor = DesktopsInteractor(DesktopsRepository)

    fun getCamera(id: String) {
        val d = desktopsInteractor.getCamera(id)
                .doOnSubscribe { viewState.showProgress(); viewState.hideButtons() }
                .subscribe(
                        {
                            viewState.handleButton(it, it.archive)
                        },
                        {
                            viewState.handleButton(null, null)
                            viewState.hideProgress()
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

}