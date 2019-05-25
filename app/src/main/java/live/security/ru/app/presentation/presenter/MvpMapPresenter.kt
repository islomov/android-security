package ru.security.live.presentation.presenter

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import io.reactivex.disposables.Disposable
import ru.security.live.data.repository.MapRepositoryImpl
import ru.security.live.domain.entity.Point
import ru.security.live.domain.interactor.MapInteractor
import ru.security.live.presentation.App
import ru.security.live.presentation.view.iview.MvpMapView
import ru.security.live.util.LoggingTool
/**
 * @author sardor
 */
@SuppressLint("CheckResult")
@InjectViewState
class MvpMapPresenter : MvpPresenter<MvpMapView>() {

    private val interactor = MapInteractor(MapRepositoryImpl)
    private var disposable1: Disposable? = null
    private var disposable2: Disposable? = null

    fun getDevices() {
        disposable1 = interactor.getPoints()
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe({ list ->
                    viewState.hideCancellableProgress()
                    viewState.updatePoints(list.filter { isValidPoint(it) })
                    viewState.setMarkers()
                }, {
                    viewState.hideCancellableProgress()
                    viewState.error(it)
                }, {})
        App.compositeDisposable.add(disposable1!!)
    }

    fun getPlaceDevices(placeId: String) {
        disposable2 = interactor.getPlacePoints(placeId)
                .subscribe({ list ->
                    viewState.updatePoints(list.filter { isValidPoint(it) })
                    viewState.setMarkers()
                }, {
                    viewState.error(it)
                }, {})
        App.compositeDisposable.add(disposable2!!)
    }

    fun getBuildingPlan(planId: String) {
        interactor.getPlan(planId)
                .doOnSubscribe { viewState.showCancellableProgress() }
                .subscribe({
                    viewState.hideCancellableProgress()
                    viewState.openPlan(it)
                }, {
                    viewState.error(it)
                    viewState.hideCancellableProgress()
                })
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable1?.dispose()
        disposable2?.dispose()
    }

    private fun isValidPoint(point: Point): Boolean {
        val lat = point.info.lat
        val lon = point.info.long
        val result = lat >= -90 && lat <= 90 && lon >= -190 && lon <= 180
        LoggingTool.log("Lat $lat Lon $lon Show $result")
        return result
    }
}