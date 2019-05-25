package ru.security.live.domain.interactor

import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.security.live.domain.entity.Plan
import ru.security.live.domain.entity.Point
import ru.security.live.domain.repository.IMapRepository
import java.util.concurrent.TimeUnit
/**
 * @author sardor
 */
class MapInteractor(
        private val repository: IMapRepository
) {

    fun getPoints(): Observable<List<Point>> {
        return repository.getPoints()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPlacePoints(placeId: String): Observable<List<Point>> {
        return repository.getPlacePoints(placeId)
                .repeatWhen { it.delay(15, TimeUnit.SECONDS) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPlan(planId: String): Single<Plan> {
        return repository.getPlan(planId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

}