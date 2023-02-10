package app.betmates.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PlayerUTest {

    @Test
    fun `should create Player instance`() {
        // given
        val user = User(id = 1L, name = "User", email = "user@gmail.com")
        val nickName = "London FC"

        // when
        val player = Player(nickName = nickName, user = user)

        // then
        assertNotNull(player)
        assertEquals(nickName, player.nickName)
        assertEquals(user, player.user)
        assertTrue { player.memberOf().isEmpty() }
    }

    @Test
    fun `should make player member of a team`() {
        // given
        val user = User(id = 1L, name = "User", email = "user@gmail.com")
        val player = Player(nickName = "Ronnie O'Sullivan", user = user)
        val team = SnookerTeam(name = "The Rocket")

        assertTrue {
            // when
            player.addToTeam(team)
        }

        // then
        assertTrue { player.memberOf().contains(team) }
        assertTrue { team.players().contains(player) }
    }

    @Test
    fun `should remove player from team`() {
        // given
        val user = User(id = 1L, name = "User", email = "user@gmail.com")
        val player = Player(nickName = "C. Ronaldo", user = user)
        val team = FootballTeam(name = "Real Madrid FC")
        player.addToTeam(team)
        assertTrue { player.memberOf().contains(team) }

        assertTrue {
            // when
            player.leaveTeam(team)
        }

        // then
        assertFalse { player.memberOf().contains(team) }
        assertFalse { team.players().contains(player) }
    }
}
