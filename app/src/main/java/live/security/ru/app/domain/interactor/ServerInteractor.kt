package ru.security.live.domain.interactor

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.security.live.domain.repository.IServerRepository
/**
 * @author sardor
 */
class ServerInteractor(
        private val repository: IServerRepository
) {
    fun getServers(): Single<Pair<String, String>> {
        return repository.getServers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun setServers(url1: String, url2: String): Completable {
        return repository.setServers(url1, url2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}