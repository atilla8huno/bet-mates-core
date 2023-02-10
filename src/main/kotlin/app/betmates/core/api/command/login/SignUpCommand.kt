package app.betmates.core.api.command.login

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.SignUpRequest
import app.betmates.core.api.dto.SignUpResponse
import app.betmates.core.db.service.UserService
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.exception.ConflictException
import kotlinx.coroutines.coroutineScope

class SignUpCommand(
    private val userService: UserService = UserServiceImpl()
) : Command<SignUpRequest, SignUpResponse> {

    override suspend fun execute(request: SignUpRequest): SignUpResponse = coroutineScope {
        val userAlreadyExists = userService.existsByEmailOrUsername(request.email, request.username)

        if (userAlreadyExists) {
            throw ConflictException("E-mail/Username already exists")
        }

        val newUser = userService.save(request.mapToDomain())

        SignUpResponse(newUser.id!!, newUser.username)
    }
}
