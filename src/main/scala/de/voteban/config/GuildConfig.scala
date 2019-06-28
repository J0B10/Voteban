package de.voteban.config

/**
  * Configuration object for a guild
  */
case class GuildConfig(
                        guildId: Long,
                        banReasons: Seq[String],
                        banReasonImages: Seq[String]
                        //Add more values here (but don't forget to also implement them in ConfigurationService)
                      ) {
}
