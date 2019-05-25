package ru.security.live.presentation.view.iview

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import ru.security.live.domain.entity.User
/**
 * @author sardor
 */
interface MenuView : BaseView {
    fun showDialog(url1: String, url2: String)
    fun success()
    fun showUser(user: User)
    fun noUser()
    @StateStrategyType(OneExecutionStateStrategy::class)
    fun logout()

    fun navigateToAuth()
}