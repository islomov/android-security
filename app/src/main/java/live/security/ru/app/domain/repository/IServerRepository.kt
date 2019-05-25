package ru.security.live.domain.repository

import io.reactivex.Completable
import io.reactivex.Single
/**
 * @author sardor
 */
interface IServerRepository {
    fun setServers(url1: String, url2: String): Completable
    fun getServers(): Single<Pair<String, String>>
}