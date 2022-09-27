package app.betmates.core.domain

import app.betmates.core.util.JwtProperties
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.security.MessageDigest
import java.util.Date

class User(
    val name: String = "",
    val email: String,
    val username: String = email.split("@")[0]
) : Base() {
    companion object {
        private val SHA3: MessageDigest = MessageDigest.getInstance("SHA3-256")
        const val TOKEN_EXPIRATION = 28800000L

        fun encrypt(password: String): String {
            val hashBytes: ByteArray = SHA3.digest(
                password.toByteArray(Charsets.UTF_8)
            )
            return hashBytes.toHex()
        }
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
        encryptedPassword = encrypt(password)
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

fun ByteArray.toHex(): String {
    val hexString = StringBuilder(2 * this.size)
    for (i in this.indices) {
        val hex = Integer.toHexString(0xff and this[i].toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString()
}
