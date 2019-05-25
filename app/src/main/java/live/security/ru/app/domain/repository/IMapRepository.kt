package ru.security.live.domain.repository

import io.reactivex.Observable
import io.reactivex.Single
import ru.security.live.domain.entity.Plan
import ru.security.live.domain.entity.Point

interface IMapRepository {
    fun getPoints(): Observable<List<Point>>
    fun getPlacePoints(placeId: String): Observable<List<Point>>
    fun getPlan(planId: String): Single<Plan>
}