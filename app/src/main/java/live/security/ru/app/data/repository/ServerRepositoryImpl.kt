package ru.security.live.data.repository

import io.reactivex.Completable
import io.reactivex.Single
import ru.security.live.data.pref.ServerPref
import ru.security.live.domain.repository.IServerRepository
/**
 * @author sardor
 */
object ServerRepositoryImpl : IServerRepository {
    override fun getServers(): Single<Pair<String, String>> {
        return Single.fromCallable {
            ServerPref.url1 to ServerPref.url2
        }
    }

    override fun setServers(url1: String, url2: String): Completable {
        return Completable.complete()
                .doOnComplete {
                    //ServerPref.url1 = url1
                    ServerPref.url2 = url2
                }
    }
}