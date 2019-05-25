package ru.security.live.domain.interactor

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.security.live.domain.entity.User
import ru.security.live.domain.repository.IUserRepository
/**
 * @author sardor
 */
class UserInteractor(
        private val repository: IUserRepository) {
    fun getUser(): Single<User> {
        return repository.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getPermissions(): Single<HashMap<String, Boolean>> {
        return repository.getPermissions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}