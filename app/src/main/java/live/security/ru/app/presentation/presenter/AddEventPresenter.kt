package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.EventsRepository
import ru.security.live.domain.entity.CreateEventData
import ru.security.live.domain.entity.EventTypeItem
import ru.security.live.domain.interactor.EventsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.AddEventView
import ru.security.live.util.LoggingTool
import java.io.File
/**
 * @author sardor
 */
@InjectViewState
@SuppressLint("CheckResult")
class AddEventPresenter : MvpPresenter<AddEventView>() {

    private val interactor = EventsInteractor(EventsRepository)

    fun getEventTypes() {
        val d = interactor.eventTypes()
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            val list = ArrayList<EventTypeItem>()
                            list.add(EventTypeItem("", ""))
                            list.addAll(it)
                            viewState.init(
                                    list)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

    fun createEvent(data: CreateEventData) {
        val d = interactor.createEvent(data)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            LoggingTool.log("Event created:id$it")
                            viewState.onEventCreated()
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

    fun uploadFile(file: File?) {
        if (file != null) {
            val d = interactor.uploadFile(file)
                    .doOnSubscribe { viewState.showCancellableProgress() }
                    .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                    .subscribe({
                        viewState.addFileId(file.hashCode(), it)
                    }, {
                        viewState.error(it)
                    })
            App.compositeDisposable.add(d)
        } else {
            viewState.error(Throwable("Не удалось сохранить скриншот"))
        }
    }
}