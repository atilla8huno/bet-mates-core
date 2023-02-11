package app.betmates.core.api.dto

import app.betmates.core.domain.Player
import app.betmates.core.domain.User
import kotlinx.serialization.Serializable

@Serializable
data class PlayerRequest(
    var id: Long = 0,
    val userId: Long,
    val nickName: String,
    val memberOf: Set<Long> = emptySet()
) : RequestDTO<Player> {
    override fun mapToDomain(): Player {
        return Player(
            nickName = this.nickName,
            user = User(this.userId)
        )
    }
}
