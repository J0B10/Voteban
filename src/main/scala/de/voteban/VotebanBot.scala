package de.voteban

import de.voteban.config.XMLConfigurationService
import de.voteban.db.JSONDatabaseService
import de.voteban.command._
import de.voteban.utils.{ConfigManager, RestartScheduler, WithLogger}
import javax.security.auth.login.LoginException
import net.dv8tion.jda.core.{JDA, JDABuilder}

object VotebanBot extends WithLogger {

  private var _jda: Option[JDA] = None
  private var restartScheduler: Option[RestartScheduler] = None
  val configService = new XMLConfigurationService
  val databaseService = new JSONDatabaseService


  def JDA: JDA = _jda.getOrElse(throw new IllegalStateException("Bot is not initialized yet"))

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
    JDA addEventListener new ConfigManager(this)
    JDA.addEventListener(VotebanCommand)
  }


  private def onShutdown(): Unit = {
    restartScheduler.foreach(_.cancel())
    JDA.shutdown()
  }
}
