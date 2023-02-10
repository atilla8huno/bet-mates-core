package app.betmates.core.domain

class FootballTeam(
    override var id: Long? = null,
    override var name: String
) : Team(
    id,
    TeamType.FOOTBALL,
    name
)
