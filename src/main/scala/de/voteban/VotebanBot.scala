package de.voteban

import de.voteban.command._
import de.voteban.config.{GuildConfig, XMLConfigurationService}
import de.voteban.db.{GuildData, JSONDatabaseService, UserData}
import de.voteban.utils.{ConfigManager, EmbedUtils, RestartScheduler, WithLogger}
import javax.security.auth.login.LoginException
import net.dv8tion.jda.core.entities.{Game, Guild, Member}
import net.dv8tion.jda.core.{JDA, JDABuilder}

object VotebanBot extends WithLogger {
  /**
    * Service that manages the config files
    */
  val configService = new XMLConfigurationService
  /**
    * Service for accessing the database file
    */
  val databaseService = new JSONDatabaseService
  private var _jda: Option[JDA] = None
  private var restartScheduler: Option[RestartScheduler] = None

  /**
    * Shortcut for getting the database content for a specific guild
    *
    * @param guild guild object form jda
    * @return the database entry for that guild
    */
  def GUILD_DATA(guild: Guild): GuildData = databaseService.database.guilds.getOrElse(guild.getIdLong, GuildData(guild.getIdLong, Map()))

  /**
    * Shortcut for getting the database content for a specific guild member
    *
    * @param member member object form jda
    * @return the database entry for that user
    */
  def USER_DATA(member: Member): UserData = GUILD_DATA(member.getGuild).users.getOrElse(member.getUser.getIdLong, UserData(member.getUser.getIdLong, 0, 0))

  /**
    * Shortcut for getting the configuration settings for a specific guild
    *
    * @param guild guild object form jda
    * @return the config for that guild or the default config if an unknown guild
    */
  def GUILD_CONFIG(guild: Guild): GuildConfig = try {
    configService.loadGuildConfig(guild.getIdLong)
  } catch {
    case e: Exception =>
      log error s"Could not load config for guild ${guild.getId}: ${e.getClass.getName} - ${e.getMessage}"
      XMLConfigurationService.DEFAULT_CONFIG(guild.getIdLong)
  }

  private[Launcher] def init(apiToken: String, restartScheduler: Option[RestartScheduler] = None): Boolean = {
    try {
      this.restartScheduler = restartScheduler
      log info "Waiting while bot is logging in..."
      _jda = Some(new JDABuilder(apiToken).build.awaitReady)
      sys addShutdownHook onShutdown
      log info "Voteban bot successfully logged in."
      onStart()
      true
    } catch {
      case e: LoginException =>
        log error s"Login failed: ${e.getMessage}"
        false
      case _: InterruptedException =>
        log warn "Login process was interrupted"
        false
    }
  }

  private def onStart(): Unit = {
    configService.loadCache()
    databaseService.loadDatabase()
    JDA addEventListener new ConfigManager
    JDA.addEventListener(VotebanCommand)
    JDA.getPresence.setGame(Game.playing(EmbedUtils.BOT_QUICK_URL))
  }

  private def onShutdown(): Unit = {
    restartScheduler.foreach(_.cancel())
    JDA.shutdown()
  }

  /**
    * @return the jda instance used by this bot
    */
  def JDA: JDA = _jda.getOrElse(throw new IllegalStateException("Bot is not initialized yet"))
}
