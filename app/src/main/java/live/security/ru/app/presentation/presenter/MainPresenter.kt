package ru.security.live.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.AuthRepositoryImpl
import ru.security.live.data.repository.UserRepositoryImpl
import ru.security.live.domain.interactor.AuthInteractor
import ru.security.live.domain.interactor.UserInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.MainView
import ru.security.live.util.permissionCodeEvents
import ru.security.live.util.permissionCodeMaps
import ru.security.live.util.permissionCodeVideomonitoring
/**
 * @author sardor
 */
@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {
    private val authInteractor = AuthInteractor(AuthRepositoryImpl)
    private val userInteractor = UserInteractor(UserRepositoryImpl)

    fun logout() {
        val d = authInteractor.logout()
                .subscribe({
                    viewState.doLogout()
                }, {})
    }

    fun getPermissions() {
        val d = userInteractor.getPermissions()
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe { t1, t2 ->
                    viewState.hideCancellableProgress()

                    if (t1 != null) {
                        viewState.showSections(t1[permissionCodeVideomonitoring], t1[permissionCodeMaps],
                                t1[permissionCodeEvents])
                    } else {
                        viewState.error(t2)
                    }
                }
        App.compositeDisposable.add(d)
    }
}