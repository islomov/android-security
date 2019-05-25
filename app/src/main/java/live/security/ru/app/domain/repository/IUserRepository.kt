package ru.security.live.domain.repository

import io.reactivex.Single
import ru.security.live.domain.entity.User
/**
 * @author sardor
 */
interface IUserRepository {
    fun getUser(): Single<User>
    fun getPermissions(): Single<HashMap<String, Boolean>>
}