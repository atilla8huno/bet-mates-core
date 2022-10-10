CREATE TABLE IF NOT EXISTS "user" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "email" VARCHAR,
  "username" VARCHAR,
  "password" VARCHAR,
  "name" VARCHAR,
  "status" VARCHAR
);

CREATE TABLE IF NOT EXISTS "player" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "nick_name" VARCHAR NOT NULL,
  "user_id" BIGINT NOT NULL,
  CONSTRAINT "FK_player.user_id"
    FOREIGN KEY ("user_id")
      REFERENCES "user"("id")
);

CREATE TABLE IF NOT EXISTS "wallet_history" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "type" VARCHAR NOT NULL,
  "value" REAL NOT NULL,
  "date_time" TIMESTAMP,
  "user_id" BIGINT NOT NULL,
  CONSTRAINT "FK_wallet_history.user_id"
    FOREIGN KEY ("user_id")
      REFERENCES "user"("id")
);

CREATE TABLE IF NOT EXISTS "competition" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "start_date" TIMESTAMP,
  "format" VARCHAR NOT NULL,
  "victory_points" INT,
  "draw_points" INT,
  "tiebreaker" VARCHAR,
  "status" VARCHAR
);

CREATE TABLE IF NOT EXISTS "team" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "name" VARCHAR NOT NULL,
  "type" VARCHAR,
  "status" VARCHAR
);

CREATE TABLE IF NOT EXISTS "betting_config" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "ticket_price" REAL NOT NULL,
  "competition_id" BIGINT NOT NULL,
  "pct_team" INT,
  "pct_admin" INT,
  CONSTRAINT "FK_betting_config.competition_id"
    FOREIGN KEY ("competition_id")
      REFERENCES "competition"("id")
);

CREATE TABLE IF NOT EXISTS "match" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "start_date_time" TIMESTAMP,
  "finish_date_time" TIMESTAMP,
  "status" VARCHAR,
  "competition_id" BIGINT NOT NULL,
  "home_team_id" BIGINT NOT NULL,
  "visitor_team_id" BIGINT NOT NULL,
  "parent_match_id" BIGINT,
  CONSTRAINT "FK_match.competition_id"
    FOREIGN KEY ("competition_id")
      REFERENCES "competition"("id"),
  CONSTRAINT "FK_match.home_team_id"
    FOREIGN KEY ("home_team_id")
      REFERENCES "team"("id"),
  CONSTRAINT "FK_match.visitor_team_id"
    FOREIGN KEY ("visitor_team_id")
      REFERENCES "team"("id"),
  CONSTRAINT "FK_match.parent_match_id"
    FOREIGN KEY ("parent_match_id")
      REFERENCES "match"("id")

);

CREATE TABLE IF NOT EXISTS "betting_history" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "qtd_tickets" INT NOT NULL,
  "status" VARCHAR,
  "user_id" BIGINT NOT NULL,
  "match_id" BIGINT NOT NULL,
  "winner_team_id" BIGINT,
  "betting_config_id" BIGINT NOT NULL,
  CONSTRAINT "FK_betting_history.user_id"
    FOREIGN KEY ("user_id")
      REFERENCES "user"("id"),
  CONSTRAINT "FK_betting_history.match_id"
    FOREIGN KEY ("match_id")
      REFERENCES "match"("id"),
  CONSTRAINT "FK_betting_history.winner_team_id"
    FOREIGN KEY ("winner_team_id")
      REFERENCES "team"("id"),
  CONSTRAINT "FK_betting_history.betting_config_id"
    FOREIGN KEY ("betting_config_id")
      REFERENCES "betting_config"("id")
);

CREATE TABLE IF NOT EXISTS "match_stats" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "match_id" BIGINT NOT NULL,
  "first_team_score" INT,
  "second_team_score" INT,
  "match_duration" TIMESTAMP,
  CONSTRAINT "FK_match_stats.match_id"
    FOREIGN KEY ("match_id")
      REFERENCES "match"("id")
);

CREATE TABLE IF NOT EXISTS "player_team" (
  "id" SERIAL NOT NULL PRIMARY KEY,
  "player_id" BIGINT NOT NULL,
  "team_id" BIGINT NOT NULL,
  CONSTRAINT "FK_player_team.player_id"
    FOREIGN KEY ("player_id")
      REFERENCES "player"("id"),
  CONSTRAINT "FK_player_team.team_id"
    FOREIGN KEY ("team_id")
      REFERENCES "team"("id")
);
