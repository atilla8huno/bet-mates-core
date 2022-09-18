package app.betmates.core.db.service

import app.betmates.core.db.entity.UserRepository
import app.betmates.core.domain.User

interface UserService : RepositoryService<User, UserRepository>
