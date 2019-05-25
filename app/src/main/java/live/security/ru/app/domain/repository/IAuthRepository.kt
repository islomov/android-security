package ru.security.live.domain.repository

import android.content.Context
import io.reactivex.Completable
import io.reactivex.Single
import ru.security.live.data.net.response.SettingsResponse
/**
 * @author sardor
 */
interface IAuthRepository {

    fun login(login: String, password: String, accountType: String, context: Context): Completable
    fun getAuthData(): Single<Pair<String, String>>
    fun logout(): Completable

    fun settings(server: String): Single<SettingsResponse>
}