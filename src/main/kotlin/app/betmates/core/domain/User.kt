package app.betmates.core.domain

class User(
    val name: String,
    val email: String,
    val username: String = email.split("@")[0]
) {
    private var status: Status = Status.ACTIVE

    fun isActive() = status == Status.ACTIVE

    fun deactivate() {
        status = Status.INACTIVE
    }
}
