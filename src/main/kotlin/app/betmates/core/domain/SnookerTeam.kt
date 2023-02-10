package app.betmates.core.domain

class SnookerTeam(
    override var id: Long? = null,
    override var name: String
) : Team(
    id,
    TeamType.SNOOKER,
    name
)
