package app.betmates.core.domain

import java.security.MessageDigest

private val sha3: MessageDigest = MessageDigest.getInstance("SHA3-256")

class User(
    val name: String,
    val email: String,
    val username: String = email.split("@")[0]
) {
    private var status: Status = Status.ACTIVE
    var encryptedPassword: String? = null
        private set

    fun isActive() = status == Status.ACTIVE

    fun deactivate() {
        status = Status.INACTIVE
    }

    fun acceptPassword(password: String) {
        val hashBytes: ByteArray = sha3.digest(
            password.toByteArray(Charsets.UTF_8)
        )
        encryptedPassword = hashBytes.toHex()
    }
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
