package ru.security.live.domain.interactor

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull
import ru.security.live.domain.entity.DesktopsItem
import ru.security.live.domain.repository.IDesktopsRepository
import ru.security.live.presentation.presenter.OnVideoDownloaded
import ru.security.live.util.DateTool
import java.util.*


/**
 * @author sardor
 */
class DesktopsInteractor(private val repository: IDesktopsRepository) {

    //--Здесь рабочие методы


    fun desktopsListData(isArchive: Boolean): Single<List<DesktopsItem>> {
        return repository.desktopsListData(isArchive).map { it.sortedByDescending { it._name } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCamerasDesktop(position: Int): Single<List<CameraFull>> {
        return repository.getCamerasDesktop(position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCamera(id: String): Single<CameraFull> {
        return repository.getCamera(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getArchiveRecord(id: String): Single<List<ArchiveItem>> {
        return repository.archive(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getArchiveStream(id: String, from: String, to: String): Single<String> {
        return repository.record(id, from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun setArchivePair(id: String, list: ArrayList<ArchiveItem>) {
        repository.setArchivePair(id, list)
    }

    fun getCameraForEvent(id: String, time: String): Single<CameraFull> {
        val from = DateTool.getEventArchiveStart(time)
        val to = DateTool.getEventArchiveEnd(time)
        return repository.getCameraForEvent(id, from, to).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }


    fun getparallelCameras(position: Int, callback: OnVideoDownloaded) {
        repository.getParallelCamerasForDesktop(position, callback)
    }

}