package app.betmates.core.domain

class FootballTeam(
    override var name: String
) : Team(
    TeamType.FOOTBALL,
    name
)
