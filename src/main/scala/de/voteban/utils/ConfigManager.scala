package de.voteban.utils

import java.io.ByteArrayOutputStream

import de.voteban.VotebanBot
import de.voteban.config.XMLConfigurationService
import net.dv8tion.jda.core.{MessageBuilder, Permission}
import net.dv8tion.jda.core.entities.{Message, User}
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener

import scala.jdk.CollectionConverters._

/**
  * Handles up & downloading of the config files through private messages
  * @param bot instance of the bot providing access to jda and the config service
  */
class ConfigManager(private val bot: VotebanBot) extends EventListener {

  /**
    * Called by JDA on any event
    * @param event any event fired by jda
    */
  override def onEvent(event: Event): Unit = {
    event match {
      case e: PrivateMessageReceivedEvent => onPM(e)
      case _ => //Default, do nothing
    }
  }

  /**
    * Called if a private message is received from any user
    *
    * @param e the Event that contains information about the received message
    */
  private def onPM(e: PrivateMessageReceivedEvent): Unit = {
    val attachments = e.getMessage.getAttachments.asScala
    if (attachments.nonEmpty) {
      attachments.filter(a => a.getFileName.toLowerCase.endsWith(".xml")).foreach(xmlFile => {
        saveConfig(xmlFile, e.getAuthor)
      })
      // language=RegExp
    } else if (e.getMessage.getContentRaw.toLowerCase.trim.matches("(/config|config\\?)")) {
      sendConfig(e.getAuthor)
    }
  }

  /**
    * Called if the bot receives a config file in a PM
    *
    * Checks if the user is allowed to send that specific file and saves it
    *
    * @param xmlFile the attached config file
    * @param author the user that did send the file
    */
  private def saveConfig(xmlFile: Message.Attachment, author: User): Unit = {
    try {
      val config = bot.configService.readGuildConfig(xmlFile.getInputStream)
       bot.JDA.getGuilds.asScala.find(g => g.getIdLong == config.guildId) match {
         case Some(guild) =>
           Option(guild.getMember(author)) match {
             case Some(member) if member.hasPermission(Permission.ADMINISTRATOR) =>
               bot.configService.saveGuildConfig(config)
               author.openPrivateChannel().complete().sendMessage(s"✅ Updated config for **${guild.getName}** _(${guild.getId})_").queue()
             case _ =>
               author.openPrivateChannel().complete()
                 .sendMessage(s"⚠ **You aren't an admin for ${guild.getName}** _(${guild.getId})_.\nMaybe the guildId is wrong?").queue()
           }
         case None =>
           author.openPrivateChannel().complete()
             .sendMessage(s"⚠ **${xmlFile.getFileName} is for an unknown guild.**\nMaybe the guildId is wrong?").queue()
       }
    } catch {
      case e: Exception =>
        author.openPrivateChannel().complete()
          .sendMessage(s"⚠ **Could not upload ${xmlFile.getFileName}** - ${e.getMessage}\nMaybe config file contains errors?").queue()
    }
  }

  /**
    * Called if `/config` command is received in a pm.
    *
    * Sends the config file for all known guilds where the user is admin
    *
    * @param author user that did send the message
    */
  private def sendConfig(author: User): Unit = {
    val guilds = bot.JDA.getGuilds.asScala.filter(g => Option(g.getMember(author)) match {
      case Some(member) if member.hasPermission(Permission.ADMINISTRATOR) => true
      case _ => false
    })
    if (guilds.nonEmpty) {
      guilds.foreach(g => {
        try {
          val output = new ByteArrayOutputStream()
          bot.configService.writeGuildConfig(bot.configService.loadGuildConfig(g.getIdLong), output)
          author.openPrivateChannel().complete().sendFile(
            output.toByteArray,
            XMLConfigurationService.guildConfigFile(g.getIdLong).getName,
            new MessageBuilder(s"Config file for **${g.getName}:**").build()).queue()
        } catch {
          case e: Exception =>
            author.openPrivateChannel().complete()
              .sendMessage(s"⚠ **Could not send config file for ${g.getName}** _(${g.getId})_** - ${e.getMessage}\nCould be an internal error 🙈").queue()
        }
      })
    } else {
      author.openPrivateChannel().complete()
        .sendMessage(s"⚠ **No guilds found where your are admin.**\nFor setting up the bot look at https://github.com/joblo2213/Voteban-t").queue()
    }
  }
}
