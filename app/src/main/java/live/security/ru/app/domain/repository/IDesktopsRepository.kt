package ru.security.live.domain.repository

import io.reactivex.Single
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull

import ru.security.live.domain.entity.DesktopsItem
import ru.security.live.presentation.presenter.OnVideoDownloaded

/**
 * @author sardor
 */
interface IDesktopsRepository {
    //--Здесь рабочие методы

    fun desktopsListData(isArchive: Boolean): Single<List<DesktopsItem>>
    fun getCamerasDesktop(position: Int): Single<List<CameraFull>>
    fun archive(id: String): Single<List<ArchiveItem>>
    fun record(id: String, from: String, to: String): Single<String>
    fun getCamera(id: String): Single<CameraFull>
    fun setArchivePair(id: String, archives: ArrayList<ArchiveItem>)


    fun getCameraForEvent(id: String, from: String, to: String): Single<CameraFull>


    fun getParallelCamerasForDesktop(position: Int, callback: OnVideoDownloaded)


}