package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.DesktopsRepository
import ru.security.live.data.repository.EventsRepository
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull
import ru.security.live.domain.entity.EventDetailed
import ru.security.live.domain.interactor.DesktopsInteractor
import ru.security.live.domain.interactor.EventsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.EventView
import ru.security.live.util.LoggingTool
/**
 * @author sardor
 */
@SuppressLint("CheckResult")
@InjectViewState
class EventPresenter(private val id: String?) : MvpPresenter<EventView>() {
    private val interactor = EventsInteractor(EventsRepository)
    private val desktopsInteractor = DesktopsInteractor(DesktopsRepository)

    lateinit var event: EventDetailed
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getEvent()
    }

    private fun getEvent() {
        if (id == null)
            return

        val d = interactor.getEvent(id)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe { result, error ->

                    if (result != null) {
                        event = result

                        LoggingTool.log("Event time ${event.time}")
                        getCamera()
                    } else {
                        viewState.hideCancellableProgress()
                        viewState.error(error)
                    }
                }
        App.compositeDisposable.add(d)
    }

    private fun getCamera() {
        if (event.camera != null) {
            val d = desktopsInteractor.getCameraForEvent(event.camera!!.cameraId, event.time)
                    .doOnSubscribe { viewState.showCancellableProgress() }
                    .subscribe(
                            {
                                viewState.showDetails(event, it, it.eventArchives)
                                //checkArchive(it)
                            },
                            {
                                viewState.showDetails(event, null, mutableListOf())
                                it.printStackTrace()
                            }
                    )
            App.compositeDisposable.add(d)
        } else {
            viewState.showDetails(event, null, mutableListOf())
        }
    }

    private fun checkArchive(cameraFull: CameraFull) {
        if (event.camera != null) {
            val id = event.camera!!.cameraId
            val d = desktopsInteractor.getArchiveRecord(id)
                    .doOnSubscribe { viewState.showCancellableProgress() }
                    .subscribe(
                            {
                                if (it.isNotEmpty())
                                    LoggingTool.log("Available from ${it.first().from} to ${it.first().to}")
                                getStream(cameraFull, it)
                            },
                            {
                                viewState.showDetails(event, null, mutableListOf())
                                it.printStackTrace()
                            }
                    )
            App.compositeDisposable.add(d)
        } else {
            viewState.showDetails(event, null, mutableListOf())
        }
    }

    private fun getStream(cameraFull: CameraFull, archiveItems: List<ArchiveItem>) {
        LoggingTool.log("Requested start time ${event.time}")
        val d = desktopsInteractor.getArchiveStream(
                cameraFull.id, event.getArchiveStartUTC(), event.getArchiveEndUTC())
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe { result, t ->
                    if (result != null) {
                        val archive = ArchiveItem(event.getArchiveStartUTC(), event.getArchiveEndUTC(), result)
                        viewState.showDetails(event, cameraFull, arrayListOf(archive))
                    }
                    if (t != null) {
                        LoggingTool.log("Archive for event not found")
                        viewState.showDetails(event, cameraFull, mutableListOf())
                    }
                }
        App.compositeDisposable.add(d)
    }


}