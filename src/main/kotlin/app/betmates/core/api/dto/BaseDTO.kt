package app.betmates.core.api.dto

import app.betmates.core.domain.Base

sealed class BaseDTO<D : Base>(
    open var id: Long?
) {
    abstract fun mapToDomain(): D
}
