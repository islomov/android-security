package ru.security.live.presentation.view.iview

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.security.live.domain.entity.ArchiveItem
import ru.security.live.domain.entity.CameraFull
/**
 * @author sardor
 */
interface DeviceView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun error(error: Throwable)

    fun showProgress()
    fun hideProgress()
    fun showButtons()
    fun hideButtons()
    fun handleButton(cameraFull: CameraFull?, archive: ArchiveItem?)
}