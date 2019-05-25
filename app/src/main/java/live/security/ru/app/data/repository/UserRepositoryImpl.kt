package ru.security.live.data.repository

import io.reactivex.Single
import ru.security.live.data.net.ApiHolder
import ru.security.live.domain.entity.User
import ru.security.live.domain.repository.IUserRepository
import ru.security.live.util.permissionCodeEvents
import ru.security.live.util.permissionCodeMaps
import ru.security.live.util.permissionCodeVideomonitoring
/**
 * @author sardor
 */
object UserRepositoryImpl : IUserRepository {
    override fun getUser(): Single<User> {
        return ApiHolder.privateApi.getUser()
                .map {
                    User("${it.lastName ?: ""}\n${it.name ?: ""}\n${it.middleName ?: ""}",
                            it.avatarUri ?: "")
                }
    }

    override fun getPermissions(): Single<HashMap<String, Boolean>> {
        return ApiHolder.privateApi.getPermissions()
                .map {
                    val result = HashMap<String, Boolean>()
                    result[permissionCodeVideomonitoring] = it.find { it.code == permissionCodeVideomonitoring }!!.permissions.isNotEmpty()
                    result[permissionCodeMaps] = it.find { it.code == permissionCodeMaps }!!.permissions.isNotEmpty()
                    result[permissionCodeEvents] = it.find { it.code == permissionCodeEvents }!!.permissions.isNotEmpty()
                    result
                }
    }
}