package app.betmates.core.api.command.login

import app.betmates.core.api.command.Command
import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignInResponse
import app.betmates.core.db.service.UserService
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.User.Companion.TOKEN_EXPIRATION
import app.betmates.core.exception.AuthenticationFailed
import kotlinx.coroutines.coroutineScope

class SignInCommand(
    private val userService: UserService = UserServiceImpl()
) : Command<SignInRequest, SignInResponse> {

    override suspend fun execute(request: SignInRequest): SignInResponse = coroutineScope {
        val user = userService.findByEmailAndPassword(request.email, request.password)
            ?: throw AuthenticationFailed("Email or password is incorrect")

        val expiresAt = System.currentTimeMillis() + TOKEN_EXPIRATION

        SignInResponse(user.username, user.generateToken(), expiresAt = expiresAt)
    }
}
