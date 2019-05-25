package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.Disposable
import ru.security.live.data.repository.DesktopsRepository
import ru.security.live.domain.entity.CameraFull
import ru.security.live.domain.interactor.DesktopsInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.VideoView
import java.io.File
import java.io.FileOutputStream

/**
 * @author sardor
 */
@InjectViewState
class VideosPresenter : MvpPresenter<VideoView>(), OnVideoDownloaded {


    private var subscribe: Disposable? = null
    private val interactor = DesktopsInteractor(DesktopsRepository)
    var id: String = "desktop"
    fun getCamerasDesktop(position: Int = 0) {
        val d = interactor.getCamerasDesktop(position)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            viewState.setVideos(it)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

    @SuppressLint("CheckResult")
    fun getParallelCameras(position: Int = 0) {
        viewState.showCancellableProgress()
        interactor.getparallelCameras(position, this)
    }

    override fun downloaded(pair: Pair<Int, CameraFull?>) {
        if (pair.first == 1 || pair.first == -1)
            viewState.hideCancellableProgress()
        viewState.addVideos(pair.second!!)
    }

    fun getCamera(id: String) {
        this.id = id
        val d = interactor.getCamera(id)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .doOnEvent { _, _ -> viewState.hideCancellableProgress() }
                .subscribe(
                        {
                            viewState.setCurrentVideo(it)
                        },
                        {
                            viewState.error(it)
                        }
                )
        App.compositeDisposable.add(d)
    }

    fun setCurrentVideo(camera: CameraFull?) {
        // Need to set archive, when archive() method is called inside DesktopsRepository,
        // it overrides all archives with last archive,we need to set archive from appropriate camera
        if (camera == null)
            return
        if (!camera.archiveAvailable)
            return
        if (camera.archive == null)
            return

        interactor.setArchivePair(id, arrayListOf(camera.archive!!))
    }

//    fun checkArchive(id: String) {
//        subscribe = interactor.getArchiveRecord(id)
//                .doOnSubscribe { viewState.showProgress() }
//                .doOnEvent { _, _ -> viewState.hideProgress() }
//                .subscribe(
//                        {
//                            viewState.onArchiveChecked(it)
//                        },
//                        {
//                            viewState.error(it)
//                        }
//                )
//    }

    override fun onDestroy() {
        super.onDestroy()
        subscribe?.dispose()
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

interface OnVideoDownloaded {
    fun downloaded(pair: Pair<Int, CameraFull?>)
}