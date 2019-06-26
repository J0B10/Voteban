package de.voteban.db

/**
  * Contains stats of a user
  *
  * @param userId        snowflake id of the user
  * @param bansReceived  how often the user was banned
  * @param bansInitiated how often the user banned other users
  */
case class UserData(userId: Long, bansReceived: Int, bansInitiated: Int)
