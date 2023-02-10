package app.betmates.core.api.dto

import app.betmates.core.domain.Base

interface RequestDTO<out D : Base> {
    fun mapToDomain(): D
}
