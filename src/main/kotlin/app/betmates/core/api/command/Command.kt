package app.betmates.core.api.command

interface Command<RQ, RS> {
    suspend fun execute(request: RQ): RS
}
