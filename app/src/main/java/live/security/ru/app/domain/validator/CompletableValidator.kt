package ru.security.live.domain.validator

import io.reactivex.Completable
/**
 * @author sardor
 */
interface CompletableValidator<in D> {
    fun validate(data: D): Completable
}