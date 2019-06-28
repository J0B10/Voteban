package de.voteban

import de.voteban.config.XMLConfigurationService
import de.voteban.db.JSONDatabaseService
import de.voteban.command._
import de.voteban.utils.{ConfigManager, RestartScheduler, WithLogger}
import javax.security.auth.login.LoginException
import net.dv8tion.jda.core.{JDA, JDABuilder}

class VotebanBot(private val apiToken: String, private val restartScheduler: Option[RestartScheduler] = None) extends WithLogger {

  private var _jda: Option[JDA] = None
  val configService = new XMLConfigurationService
  val databaseService = new JSONDatabaseService


  def JDA: JDA = _jda.getOrElse(throw new IllegalStateException("Bot is not initialized yet"))

  def init(): Boolean = {
    try {
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

  def onStart(): Unit = {
    configService.loadCache()
    databaseService.loadDatabase()
    JDA addEventListener new ConfigManager(this)
    JDA.addEventListener(VotebanCommand)
  }


  def onShutdown(): Unit = {
    JDA.shutdown()
  }
}
