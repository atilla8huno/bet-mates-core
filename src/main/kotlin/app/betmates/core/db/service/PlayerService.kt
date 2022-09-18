package app.betmates.core.db.service

import app.betmates.core.db.entity.PlayerRepository
import app.betmates.core.domain.Player

interface PlayerService : RepositoryService<Player, PlayerRepository>
