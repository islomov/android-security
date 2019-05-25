package ru.security.live.domain.validator

import io.reactivex.Single
/**
 * @author sardor
 */
interface SingleValidator<D> {
    fun validate(data: D): Single<D>
}