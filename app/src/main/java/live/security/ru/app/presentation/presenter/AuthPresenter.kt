package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import android.content.Context
import retrofit2.HttpException
import ru.security.live.data.pref.ServerPref
import ru.security.live.data.pref.UserPref
import ru.security.live.data.repository.AuthRepositoryImpl
import ru.security.live.data.repository.UserRepositoryImpl
import ru.security.live.domain.interactor.AuthInteractor
import ru.security.live.domain.interactor.UserInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.AuthView
import ru.security.live.util.INVALID_TOKEN
/**
 * @author sardor
 */
@SuppressLint("CheckResult")
class AuthPresenter(val context: Context, val viewState: AuthView) {
    private var interactor = AuthInteractor(AuthRepositoryImpl)
    private val userInteractor = UserInteractor(UserRepositoryImpl)

    fun getAuthData() {
        val d = interactor.getAuthData()
                .subscribe({
                    if (it != null) {
                        viewState.initAuthData(it.first, it.second)
                    }
                }, {})
        App.compositeDisposable.add(d)
    }

    fun tryLogin(login: String, password: String) {
        val d = interactor.login(login, password, context)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe({
                    getUser()
                }) {
                    it.printStackTrace()
                    viewState.hideCancellableProgress()
                    viewState.error(if (it is HttpException) Throwable("Неверная связка логина и пароля")
                    else Throwable("Отсутствует подключение к сети"))
                }
        App.compositeDisposable.add(d)
    }

    fun tryLdap(login: String, password: String) {
        val d = interactor.loginLdap(login, password, context)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe({
                    getUser()
                }, {
                    it.printStackTrace()
                    viewState.hideCancellableProgress()
                    viewState.error(if (it is HttpException) Throwable("Неверная связка логина и пароля")
                    else Throwable("Отсутствует подключение к сети"))
                })
        App.compositeDisposable.add(d)
    }

    fun getUser() {
        if (ServerPref.token != INVALID_TOKEN) {
            val d = userInteractor.getUser()
                    .doOnSubscribe { viewState.showCancellableProgress() }
                    .subscribe({
                        viewState.hideCancellableProgress()
                        UserPref.name = it.name
                        UserPref.avatarUri = it.imageUrl
                        viewState.success()
                    }, {
                        viewState.hideCancellableProgress()
                        viewState?.error(it)
                    })
            App.compositeDisposable.add(d)
        }
    }

    fun updateInteractor() {
        interactor = AuthInteractor(AuthRepositoryImpl)
    }
}