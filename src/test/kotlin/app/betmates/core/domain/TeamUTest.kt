package app.betmates.core.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class TeamUTest {

    @Test
    fun `should create FootballTeam instance`() {
        // given
        val name = "London FC"

        // when
        val team: Team = FootballTeam(name)

        // then
        assertNotNull(team)
        assertEquals(name, team.name)
        assertEquals(Status.ACTIVE, team.status)
        assertEquals(TeamType.FOOTBALL, team.type)
    }

    @Test
    fun `should deactivate a team`() {
        // given
        val team: Team = SnookerTeam("The Rocket")
        assertEquals(Status.ACTIVE, team.status)

        // when
        team.deactivate()

        // then
        assertEquals(Status.INACTIVE, team.status)
    }

    @Test
    fun `should make player member of the team`() {
        // given
        val player = Player("C. Ronaldo")
        val team = FootballTeam("Real Madrid FC")

        assertTrue {
            // when
            team.addPlayer(player)
        }

        // then
        assertTrue { team.players().contains(player) }
        assertTrue { player.memberOf().contains(team) }
    }

    @Test
    fun `should remove player from the team`() {
        // given
        val player = Player("C. Ronaldo")
        val team = FootballTeam("Real Madrid FC")
        team.addPlayer(player)
        assertTrue { team.players().contains(player) }
        assertTrue { player.memberOf().contains(team) }

        assertTrue {
            // when
            team.removePlayer(player)
        }

        // then
        assertFalse { team.players().contains(player) }
        assertFalse { player.memberOf().contains(team) }
    }
}
