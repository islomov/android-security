package ru.security.live.domain.validator

import io.reactivex.Completable
/**
 * @author sardor
 */
class AuthValidator : CompletableValidator<Pair<String, String>> {
    override fun validate(data: Pair<String, String>): Completable {
        return Completable.defer {
            if (data.first.isEmpty() || data.second.isEmpty()) Completable.error(Throwable("Ошибка"))
            else Completable.complete()
        }
    }

}