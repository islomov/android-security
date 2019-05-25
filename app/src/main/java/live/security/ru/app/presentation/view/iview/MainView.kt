package ru.security.live.presentation.view.iview
/**
 * @author sardor
 */
interface MainView : BaseView {
    fun doLogout()
    fun showSections(monitoring: Boolean?, map: Boolean?, events: Boolean?)
}