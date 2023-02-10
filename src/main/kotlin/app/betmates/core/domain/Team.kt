package app.betmates.core.domain

sealed class Team(
    override var id: Long? = null,
    val type: TeamType,
    open var name: String,
    private val players: MutableSet<Player> = mutableSetOf()
) : Base(id) {
    var status: Status = Status.ACTIVE
        private set

    fun isActive() = status == Status.ACTIVE

    fun deactivate() {
        status = Status.INACTIVE
    }

    fun addPlayer(player: Player): Boolean = players.add(player)
        .and(if (this !in player.memberOf()) player.addToTeam(this) else true)

    fun removePlayer(player: Player): Boolean = players.remove(player)
        .and(if (this in player.memberOf()) player.leaveTeam(this) else true)

    fun players(): Set<Player> = players.toSet()
}

enum class TeamType {
    FOOTBALL,
    SNOOKER
}
