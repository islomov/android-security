package ru.security.live.domain.interactor

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.security.live.data.net.response.SettingsResponse
import ru.security.live.domain.repository.IAuthRepository
import ru.security.live.domain.validator.AuthValidator
import ru.security.live.util.ACCOUNT_TYPE_LDAP
import ru.security.live.util.ACCOUNT_TYPE_LOGIN
/**
 * @author sardor
 */
class AuthInteractor(
        private val repository: IAuthRepository) {
    private val validator = AuthValidator()

    fun login(login: String, password: String, context: Context): Completable {
        // Просто попытка потыкать валидаторы
        //
        // такое себе
        return validator.validate(login to password)
                .andThen(repository.login(login, password, ACCOUNT_TYPE_LOGIN, context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun loginLdap(login: String, password: String, context: Context): Completable {
        // Просто попытка потыкать валидаторы
        //
        // такое себе
        return validator.validate(login to password)
                .andThen(repository.login(login, password, ACCOUNT_TYPE_LDAP, context))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    // Возвращает пару логин + парол
    fun getAuthData(): Single<Pair<String, String>> {
        return repository.getAuthData()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun logout(): Completable {
        return repository.logout()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun settings(server: String): Single<SettingsResponse> {
        return repository.settings(server)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}