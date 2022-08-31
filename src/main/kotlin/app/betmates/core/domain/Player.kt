package app.betmates.core.domain

class Player(
    val nickName: String,
    private val memberOf: MutableSet<Team> = mutableSetOf()
) {
    fun addToTeam(team: Team): Boolean = memberOf.add(team)
        .and(if (this !in team.players()) team.addPlayer(this) else true)

    fun leaveTeam(team: Team) = memberOf.remove(team)
        .and(if (this in team.players()) team.removePlayer(this) else true)

    fun memberOf() = memberOf.toSet()
}
