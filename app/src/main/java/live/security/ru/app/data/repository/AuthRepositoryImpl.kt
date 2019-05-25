package ru.security.live.data.repository

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import ru.security.live.data.account.AccountManagerHelper
import ru.security.live.data.net.ApiHolder
import ru.security.live.data.net.request.LoginRequest
import ru.security.live.data.net.response.SettingsResponse
import ru.security.live.data.pref.ServerPref
import ru.security.live.data.pref.UserPref
import ru.security.live.domain.repository.IAuthRepository
import ru.security.live.util.ConnectivityTool
import ru.security.live.util.LoggingTool
/**
 * @author sardor
 */
object AuthRepositoryImpl : IAuthRepository {

    override fun login(login: String, password: String, accountType: String, context: Context): Completable {
        val request = LoginRequest(login, password, accountType)
        return if (ConnectivityTool.isNetworkAvailable()) {
            ApiHolder.publicApi(context).login(request)
                    .flatMapCompletable {
                        LoggingTool.log("Message ${it.message}")
                        AccountManagerHelper.createAccount(login, password)
                        AccountManagerHelper.setToken(it.tokenId!!)
                        ServerPref.token = it.tokenId
                        Completable.complete()
                    }
        } else {
            Completable.error(Throwable("Отсутствует подключение к сети"))
        }
    }

    override fun getAuthData(): Single<Pair<String, String>> {
        return Single.fromCallable {
            AccountManagerHelper.getAuthData()
        }
    }

    override fun logout(): Completable {
        return Completable.complete()
                .doOnComplete {
                    UserPref.name = "Неавторизованный\nпользователь"
                    UserPref.avatarUri = ""
                    AccountManagerHelper.removeAccount()
                }
    }

    override fun settings(server: String): Single<SettingsResponse> {
        return ApiHolder.publicApi.getSettings("http://${server}/authentication/settings").doOnSuccess {
            if (it != null) {
                ServerPref.url1 = it.mapSettings.tilesUri
                ServerPref.url2 = server
            }
        }
    }


}