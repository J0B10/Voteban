package de.voteban.db

/**
  * Contains all data
  *
  * @param guilds guilds that are connected to the bot
  */
case class Database(guilds: Map[Long, GuildData])
