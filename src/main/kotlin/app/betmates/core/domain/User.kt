package app.betmates.core.domain

import app.betmates.core.util.JwtProperties
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.toxicbakery.bcrypt.Bcrypt
import java.util.Date

class User(
    override var id: Long? = null,
    val name: String = "",
    val email: String = "",
    val username: String = email.split("@")[0]
) : Base(id) {
    companion object {
        const val TOKEN_EXPIRATION = 60000L
        const val SALT_ROUNDS = 5
    }

    var status: Status = Status.ACTIVE
        private set
    var encryptedPassword: String? = null
        private set

    fun isActive() = status == Status.ACTIVE

    fun deactivate() {
        status = Status.INACTIVE
    }

    fun acceptPassword(password: String) {
        encryptedPassword = Bcrypt.hash(password, SALT_ROUNDS).toString(Charsets.UTF_8)
    }

    fun generateToken(): String = JWT.create()
        .withAudience(JwtProperties.audience)
        .withIssuer(JwtProperties.issuer)
        .withClaim("username", username)
        .withExpiresAt(Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
        .sign(Algorithm.HMAC256(JwtProperties.secret))

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is User) return false

        return email == other.email
    }

    override fun hashCode(): Int = email.hashCode()
}
