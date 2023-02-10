package app.betmates.core.domain

sealed class Base(
    open var id: Long? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Base) return false
        if (id == null || other.id == null) return false

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}

enum class Status {
    ACTIVE,
    INACTIVE
}
