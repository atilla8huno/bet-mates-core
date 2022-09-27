package app.betmates.core.api.command

import app.betmates.core.api.dto.SignInRequest
import app.betmates.core.api.dto.SignInResponse
import app.betmates.core.db.service.UserService
import app.betmates.core.db.service.impl.UserServiceImpl
import app.betmates.core.domain.User.Companion.TOKEN_EXPIRATION
import app.betmates.core.domain.User.Companion.encrypt
import kotlinx.coroutines.coroutineScope
import java.util.*

class SignInCommand(
    private val userService: UserService = UserServiceImpl()
) : Command<SignInRequest, SignInResponse> {

    override suspend fun execute(request: SignInRequest): SignInResponse = coroutineScope {
        val user = userService.findByUsernameAndPassword(request.email, encrypt(request.password))
        requireNotNull(user) { "Email or password is incorrect." }

        val expiresAt = System.currentTimeMillis() + TOKEN_EXPIRATION

        SignInResponse(user.username, user.generateToken(), expiresAt = expiresAt)
    }
}
