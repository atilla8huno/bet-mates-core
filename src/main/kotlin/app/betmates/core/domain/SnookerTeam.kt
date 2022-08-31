package app.betmates.core.domain

class SnookerTeam(
    override var name: String
) : Team(
    TeamType.SNOOKER,
    name
)
