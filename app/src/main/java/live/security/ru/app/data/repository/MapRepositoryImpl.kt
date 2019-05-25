package ru.security.live.data.repository

import io.reactivex.Observable
import io.reactivex.Single
import ru.security.live.data.net.ApiHolder
import ru.security.live.domain.entity.*
import ru.security.live.domain.repository.IMapRepository
import ru.security.live.util.TYPE_CODE_CAMERA
import ru.security.live.util.TYPE_CODE_CONTROLLER
import ru.security.live.util.TYPE_CODE_LIST
import ru.security.live.util.TYPE_CODE_SENSOR
/**
 * @author sardor
 */
object MapRepositoryImpl : IMapRepository {

    override fun getPoints(): Observable<List<Point>> {
        return ApiHolder.privateApi.getMapPoints()
                .flatMap {
                    val list = mutableListOf<Point>()
                    for (item in it.itemSet?.items!!) {
                        val info = Info(
                                item?.typeCode ?: "",
                                item?.id ?: "",
                                item?.events ?: mutableListOf(),
                                item?.name ?: "",
                                item?.geoCoordinates?.latitude ?: 0.0,
                                item?.geoCoordinates?.longitude ?: 0.0,
                                item?.locationId ?: "")

                        val point = when (item?.typeCode ?: 0) {
                            TYPE_CODE_CAMERA, TYPE_CODE_SENSOR, TYPE_CODE_CONTROLLER -> {
                                Device(
                                        info,
                                        item?.surveillanceScene ?: "",
                                        item?.placementDescription ?: "",
                                        if (item?.incidents != null) item.incidents[0]?.count
                                                ?: 0 else 0,
                                        if (item?.incidents != null && item.incidents.size > 1) item.incidents[1]?.count
                                                ?: 0 else 0,
                                        item?.state ?: "",
                                        item?.availability != null && item.availability != "red")
                            }
                            else -> Building(info, item?.placementDescription
                                    ?: "", item?.planId ?: "")
                        }
                        list.add(point)
                    }
                    Observable.just(list)
                }.flatMap { list ->
                    val result = HashMap<Pair<Double, Double>, Point>()

                    for (item in list) {
                        val sameList = list.filter { isTheSamePlace(item.info, it.info) }

                        if (sameList.size == 1) {
                            result[item.info.long to item.info.lat] = item
                        } else if (sameList.size > 1) {
                            result[item.info.long to item.info.lat] = Cluster(Info(TYPE_CODE_LIST, "", mutableListOf(), "",
                                    item.info.lat, item.info.long, ""),
                                    sameList)
                        }
                    }
                    Observable.just(ArrayList<Point>(result.values))
                }

//                .map {
//                    it.itemSet?.items?.map {
//                        val info = Info(
//                                it!!.typeCode!!,
//                                it.id!!,
//                                it.name!!,
//                                it.geoCoordinates!!.latitude!!,
//                                it.geoCoordinates.longitude!!,
//                                it.locationId)
//
//                        when(it.typeCode){
//                            TYPE_CODE_CAMERA, TYPE_CODE_SENSOR, TYPE_CODE_CONTROLLER -> {
//                                Device(
//                                        info,
//                                        it.surveillanceScene!!,
//                                        it.placementDescription!!,
//                                        if (it.incidents != null) it.incidents[0]?.count else 0,
//                                        if (it.incidents != null) it.incidents[1]?.count else 0,
//                                        it.state!!,
//                                        it.availability != null && it.availability != "red")
//                            }
//                            else -> Building(info, it.placementDescription, it.planId)
//                        }
//                    }
//                }.map {list ->
//                    //сохраняем точки с парой долготы и широты - на 1 паре координат только 1 уникальная точка
//                    val result = HashMap<Pair<Double, Double>, Point>()
//
//                    for (item in list){
//                        val sameList = list.filter { isTheSamePlace(item.info, it.info) }
//
//                        if (sameList.size == 1){
//                            result[item.info.long to item.info.long] = item
//                        }else if (sameList.size > 1){
//                            result[item.info.long to item.info.long] = Cluster(Info(TYPE_CODE_LIST, "", "",
//                                    item.info.lat, item.info.long, ""),
//                                    sameList)
//                        }
//                    }
//                    val array = ArrayList<Point>()
//                    array.addAll(result.values)
//                    array
//                }
    }

    override fun getPlacePoints(placeId: String): Observable<List<Point>> {
        return ApiHolder.privateApi.getBuildingPlan(placeId)
                .map {
                    it.points?.map {
                        val info = Info(
                                it.typeCode,
                                it.id,
                                it.events ?: mutableListOf(),
                                it.name,
                                it.x!!,
                                it.y!!,
                                it.locationId)

                        Device(
                                info,
                                it.surveillanceScene,
                                it.placementDescription,
                                if (it.incidents != null && it.incidents.isNotEmpty()) it.incidents[0].count else 0,
                                if (it.incidents != null && it.incidents.size > 1) it.incidents[1].count else 0,
                                it.state,
                                it.availability != null &&
                                        it.availability != "red")
                    }
                }
    }

    override fun getPlan(planId: String): Single<Plan> {
        return ApiHolder.privateApi.getBuildingPlan(planId)
                .map {
                    Plan(it.tileUriTemplate ?: "", planId)
                }.singleOrError()
    }

    private fun isTheSamePlace(info1: Info, info2: Info): Boolean {
        return info1.lat == info2.lat && info1.long == info2.long
    }
}