package ru.security.live.data.repository

import android.annotation.SuppressLint
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import ru.security.live.data.net.ApiHolder
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull
import ru.security.live.domain.entity.DesktopsCamItem
import ru.security.live.domain.entity.DesktopsItem
import ru.security.live.domain.repository.IDesktopsRepository
import ru.security.live.presentation.App
import ru.security.live.presentation.presenter.OnVideoDownloaded
import ru.security.live.util.LoggingTool
import ru.security.live.util.ioExectuor
import ru.security.live.util.parseTime

/**
 * @author sardor
 */
object DesktopsRepository : IDesktopsRepository {


    private var desktops = ArrayList<DesktopsItem>()
    private var archive = Pair("", ArrayList<ArchiveItem>()) // Pair(id камеры, список архивных записей)

    //--Здесь рабочие методы
    override fun desktopsListData(isArchive: Boolean): Single<List<DesktopsItem>> {
        if (isArchive) {
            return if (archive.second.isNotEmpty()) Single.just(archive.second)
            else Single.error(Throwable("ArchdesktopsListDataive had not been downloaded"))
        } else {

            return if (desktops.isNotEmpty()) Single.just(desktops)
            else desktopsData()
        }
    }

    override fun getCamerasDesktop(position: Int): Single<List<CameraFull>> {
        return desktopsData()
                .flatMap { response ->
                    if (response.isNotEmpty()) {
                        val desktopItem = desktops[position]
                        Single.concat((desktopItem as DesktopsCamItem).cameras.map {
                            val camera = getCamera(it)
                            camera
                        })
                                .toList()
                    } else {
                        Single.error(Throwable("Список десктопов пуст"))
                    }
                }
    }

    @SuppressLint("CheckResult")
    override fun getParallelCamerasForDesktop(position: Int, callback: OnVideoDownloaded) {
        App.compositeDisposable.add(desktopsData()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe({ list ->
                    val desktopItem = desktops[position]
                    var totalCount = (desktopItem as DesktopsCamItem).cameras.size
                    desktopItem.cameras.map {
                        ioExectuor.execute {
                            LoggingTool.log("Total cameras:${totalCount}")

                            App.compositeDisposable.add(getCamera(it)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeOn(Schedulers.newThread()).subscribe({
                                        totalCount--
                                        callback.downloaded(Pair(totalCount, it))
                                    }, { err ->
                                        callback.downloaded(Pair(-1, null))
                                    }))
                        }

                    }
                }, { err ->
                    callback.downloaded(Pair(-1, null))
                }))
    }

    override fun getCamera(id: String): Single<CameraFull> {
        val archive = archive(id)
        val info = ApiHolder.privateApi.getCamera(id)
        return Single.zip(info, archive.onErrorReturn { arrayListOf() }, BiFunction { t1, t2 ->
            val camera = t1.toCameraFull()
            camera.archiveAvailable = t2.isNotEmpty()
            LoggingTool.log("Camera archive available:${camera.archiveAvailable}, size: ${t2.size}")
            if (camera.archiveAvailable)
                camera.archive = t2[0]
            return@BiFunction camera
        })
    }

    private fun desktopsData(): Single<List<DesktopsItem>> {
        if (desktops.isNotEmpty())
            return Single.just(desktops)

        return ApiHolder.privateApi.getDesktopsList()
                .map {
                    it.itemSet.items
                            .map { it.toDesktopsItem() }
                }
                .doOnSuccess {
                    desktops = ArrayList(it)
                }
    }

    override fun archive(id: String): Single<List<ArchiveItem>> {
        return ApiHolder.privateApi.getArchiveList(id)

                .flatMap {
                    Single.concat(it.map {
                        val item = it;record(id, it.from, it.to)
                            .doOnSuccess {
                                item.url = it
                                item._name = parseTime(item.from, item.to)
                            }.map { item }
                    }).toList()
                }
                .doOnSuccess {
                    archive = Pair(id, ArrayList(it))
                }
    }

    override fun record(id: String, from: String, to: String): Single<String> {
        LoggingTool.log("Camera ID $id")
        return ApiHolder.privateApi.getRecord(id, from, to).map {
            it.broadcastings.first().url
        }
    }

    override fun setArchivePair(id: String, archives: ArrayList<ArchiveItem>) {
        archive = Pair(id, archives)
    }

    override fun getCameraForEvent(id: String, from: String, to: String): Single<CameraFull> {
        val archive = getArchiveForEvent(id, from, to)
        val info = ApiHolder.privateApi.getCameraForEvent(id)
        return Single.zip(info, archive.onErrorReturn { arrayListOf() }, BiFunction { t1, t2 ->
            val camera = t1.toCameraFull()
            camera.archiveAvailable = t2.isNotEmpty()
            if (camera.archiveAvailable)
                camera.eventArchives = t2
            return@BiFunction camera
        })
    }

    private fun getArchiveForEvent(id: String, from: String, to: String): Single<List<ArchiveItem>> {
        return ApiHolder.privateApi.getArchivesForEvent(id, from, to)
                .flatMap { response ->
                    Observable.fromIterable(response.broadcastings).map {
                        val item = ArchiveItem(from, to, it.url!!, it.weight)
                        item._name = parseTime(item.from, item.to)
                        item
                    }.toList()
                }
                .doOnSuccess {
                    archive = Pair(id, arrayListOf(it.asSequence().minBy { archive -> archive.weight }!!))
                }
    }

}