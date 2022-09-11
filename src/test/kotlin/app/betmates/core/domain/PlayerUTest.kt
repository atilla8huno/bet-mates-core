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
        val nickName = "London FC"

        // when
        val player = Player(nickName)

        // then
        assertNotNull(player)
        assertEquals(nickName, player.nickName)
        assertTrue { player.memberOf().isEmpty() }
    }

    @Test
    fun `should make player member of a team`() {
        // given
        val player = Player("Ronnie O'Sullivan")
        val team = SnookerTeam("The Rocket")

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
        val player = Player("C. Ronaldo")
        val team = FootballTeam("Real Madrid FC")
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

    @Test
    fun `should associate Player to an User`() {
        // given
        val player = Player("CR7")
        val user = User(
            name = "Cristiano Ronaldo",
            email = "pai@cr7.com"
        )

        // when
        player.associateTo(user)

        // then
        assertNotNull(player.user)
        assertEquals(user, player.user)
    }
}
