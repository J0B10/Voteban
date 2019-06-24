package de.voteban.command

import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener

import scala.jdk.CollectionConverters._

/**
  * A command that can be executed by a user by sending a message that starts with a `/` followed by the command name (or an alias)
  *
  * Commands are registered by using `JDA.addEventListener()`
  *
  * @param name the name of the command
  * @param _aliases aliases that can be used instead of the name
  * @param requiredPermissions permissions that the user needs to execute the command
  */
abstract class Command(val name: String, private val _aliases: Seq[String], val requiredPermissions: Permission*) extends EventListener {

  def this(_name: String, _aliases: String*) = {
    this(_name, _aliases.toSeq)
  }

  /**
    * All aliases including the default name (always lowercase)
    */
  val aliases: Seq[String] = name +: _aliases map (_.toLowerCase)

  /**
    * Override to implement what is done on command
    *
    * @param message message that initiated the command
    */
  protected def execute(message: Message)

  override def onEvent(e: Event): Unit = {
    e match {
      case event: GuildMessageReceivedEvent =>
        aliases.find(a => event.getMessage.getContentRaw.trim.toLowerCase.startsWith(s"/$a ")) match {
          case Some(alias) =>
            if (event.getMember.hasPermission(event.getChannel, requiredPermissions.asJava)) {
              execute(event.getMessage)
            }
          case _ => //Default, do nothing
        }
      case _ => //Default, do nothing
    }
  }
}
