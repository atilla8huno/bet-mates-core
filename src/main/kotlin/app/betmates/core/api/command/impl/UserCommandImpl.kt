package app.betmates.core.api.command.impl

import app.betmates.core.api.command.UserCommand
import app.betmates.core.api.dto.SignUpResponse
import app.betmates.core.api.dto.UserRequest
import app.betmates.core.db.service.UserService
import kotlinx.coroutines.coroutineScope

class UserCommandImpl(
    private val userService: UserService
) : UserCommand {

    override suspend fun signUp(request: UserRequest): SignUpResponse = coroutineScope {
        val newUser = userService.save(request.mapToDomain())

        SignUpResponse(newUser.id!!, newUser.username)
    }
}
