package app.betmates.core.api.command

import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.api.dto.SignUpResponse
import app.betmates.core.db.service.UserService
import kotlinx.coroutines.coroutineScope

class SignUpCommand(
    private val userService: UserService
) : Command<SignUpRequest, SignUpResponse> {

    override suspend fun execute(request: SignUpRequest): SignUpResponse = coroutineScope {
        val newUser = userService.save(request.mapToDomain())

        SignUpResponse(newUser.id!!, newUser.username)
    }
}
