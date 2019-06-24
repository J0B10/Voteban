package de.voteban

import de.voteban.utils.{RestartScheduler, WithLogger}
import javax.security.auth.login.LoginException
import net.dv8tion.jda.core.{JDA, JDABuilder}

class VotebanBot(private val apiToken: String, private val restartScheduler: Option[RestartScheduler] = None) extends WithLogger {

  private var _jda: Option[JDA] = None

  def JDA: JDA = _jda.getOrElse(throw new IllegalStateException("Bot is not initialized yet"))

  def init(): Boolean = {
    try {
      log info "Waiting while bot is logging in..."
      _jda = Some(new JDABuilder(apiToken).build.awaitReady)
      sys addShutdownHook onShutdown
      log info "Voteban bot successfully logged in."
      onStart
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

  }


  def onShutdown(): Unit = {

  }
}
