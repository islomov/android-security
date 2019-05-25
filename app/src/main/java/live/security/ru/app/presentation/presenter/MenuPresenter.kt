package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.AuthRepositoryImpl
import ru.security.live.data.repository.ServerRepositoryImpl
import ru.security.live.data.repository.UserRepositoryImpl
import ru.security.live.domain.interactor.AuthInteractor
import ru.security.live.domain.interactor.ServerInteractor
import ru.security.live.domain.interactor.UserInteractor
import ru.security.live.presentation.view.iview.MenuView
/**
 * @author sardor
 */
@InjectViewState
class MenuPresenter : MvpPresenter<MenuView>() {
    private val serverInteractor = ServerInteractor(ServerRepositoryImpl)
    private val userInteractor = UserInteractor(UserRepositoryImpl)
    private val authInteractor = AuthInteractor(AuthRepositoryImpl)

    @SuppressLint("CheckResult")
    fun showDialog() {
        serverInteractor.getServers()
                .subscribe({
                    viewState.showDialog(it.first, it.second)
                }, {})
    }

    @SuppressLint("CheckResult")
    fun setServer(url1: String, url2: String) {
        serverInteractor.setServers(url1, url2)
                .subscribe({
                    viewState.success()
                }, {})
    }

    @SuppressLint("CheckResult")
    fun logout() {
        authInteractor.logout()
                .subscribe {
                    viewState?.logout()
                }
    }

    @SuppressLint("CheckResult")
    fun settings(server: String) {
        authInteractor.settings(server)
                .doOnSubscribe {
                    viewState.showCancellableProgress()
                }.doOnEvent { _, _ ->
                    viewState.hideCancellableProgress()
                }
                .subscribe({
                    viewState.navigateToAuth()
                }, { err -> viewState.error(err) })
    }
}