package app.betmates.core.domain

class Player(
    val nickName: String,
    private val memberOf: MutableSet<Team> = mutableSetOf(),
    val user: User
) : Base() {
    fun <T : Team> addToTeam(team: T): Boolean = memberOf.add(team)
        .and(if (this !in team.players()) team.addPlayer(this) else true)

    fun <T : Team> leaveTeam(team: T) = memberOf.remove(team)
        .and(if (this in team.players()) team.removePlayer(this) else true)

    fun memberOf() = memberOf.toSet()
}
