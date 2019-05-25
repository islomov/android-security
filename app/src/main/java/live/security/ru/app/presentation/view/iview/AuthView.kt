package ru.security.live.presentation.view.iview

import ru.security.live.domain.entity.User
/**
 * @author sardor
 */
interface AuthView : BaseView {
    fun success()
    fun initAuthData(login: String, password: String)
    fun toggleButtons(enable: Boolean)
    fun saveUser(it: User)
}