package de.voteban.db

/**
  * Contains the stats of all users from a guild
  *
  * @param guildId snowflake id of the guild
  * @param users   map of all users in that guild
  */
case class GuildData(guildId: Long, users: Map[Long, UserData])
