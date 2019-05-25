package ru.security.live.presentation.view.iview

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
/**
 * @author sardor
 */
interface BaseView : MvpView {
    @StateStrategyType(SkipStrategy::class)
    fun error(error: Throwable)

    fun showCancellableProgress()
    fun hideCancellableProgress()
    fun showProgress()
    fun hideProgress()
    fun setUpToolbar()
    fun setUpHomeToolbar()

    fun showPositionalProgress()
    fun hidePositionalProgress()
}