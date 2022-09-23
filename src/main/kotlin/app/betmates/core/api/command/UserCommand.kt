package app.betmates.core.api.command

import app.betmates.core.api.dto.SignUpResponse
import app.betmates.core.api.dto.UserRequest

interface UserCommand {
    suspend fun signUp(request: UserRequest): SignUpResponse
}
