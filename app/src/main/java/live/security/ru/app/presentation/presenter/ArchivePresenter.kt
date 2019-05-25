package ru.security.live.presentation.presenter

import android.content.Context
import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import ru.security.live.data.repository.DesktopsRepository
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.interactor.DesktopsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.ArchiveView
import ru.security.live.util.DateTool.Companion.formatForRequest
import ru.security.live.util.DateTool.Companion.fullDateMsNoZ
import ru.security.live.util.DateTool.Companion.getCalendar
import ru.security.live.util.LoggingTool
import java.io.File
import java.io.FileOutputStream
import java.util.*
/**
 * @author sardor
 */
@InjectViewState
class ArchivePresenter : MvpPresenter<ArchiveView>() {
    private val interactor = DesktopsInteractor(DesktopsRepository)
    lateinit var endCalendar: Calendar

    fun getStream(id: String, archiveItem: ArchiveItem) {
        viewState.pausePlayer()
        LoggingTool.log("From ${archiveItem.from} to ${archiveItem.to}")
        val d = interactor.getArchiveStream(id, archiveItem.from, archiveItem.to)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe { result, t ->
                    if (result != null) {
                        viewState.updateLink(result)
                    }
                    if (t != null) {
                        viewState.error(Throwable("Произошла ошибка трансляции видеопотока"))
                    }
                }
        App.compositeDisposable.add(d)
    }

    fun getStream(id: String, from: String, to: String) {

        var startDate = from
        LoggingTool.log("Get stream for range from\n$from\n$to")
        val calendar1 = getCalendar(from, fullDateMsNoZ)
        if (calendar1.timeInMillis >= endCalendar.timeInMillis) {
            calendar1.timeInMillis = endCalendar.timeInMillis
            calendar1.set(Calendar.MINUTE, 0)
        }
        startDate = formatForRequest(calendar1, timeZone = TimeZone.getTimeZone("UTC"))
        viewState.pausePlayer()
        LoggingTool.log("getStream startDate:$startDate; to:$to;id:${id}")
        val d = interactor.getArchiveStream(id, startDate, to)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe { result, t ->
                    if (result != null) {
                        viewState.updateLink(result)
                    }
                    if (t != null) {
                        viewState.error(Throwable("Произошла ошибка трансляции видеопотока"))
                    }
                    viewState.hideCancellableProgress()
                }
        App.compositeDisposable.add(d)
    }

    fun saveFile(context: Context, bitmap: Bitmap): String {
        val folder = File(context.filesDir, "img")
        folder.mkdir()
        val file = File(folder, "video_thumbnail.jpg")
        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()
        bitmap.recycle()
        return file.absolutePath
    }


}