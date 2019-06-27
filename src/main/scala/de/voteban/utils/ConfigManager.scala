package de.voteban.utils

import de.voteban.VotebanBot
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.{Message, User}
import net.dv8tion.jda.core.events.Event
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.core.hooks.EventListener

import scala.jdk.CollectionConverters._

class ConfigManager(private val bot: VotebanBot) extends EventListener {

  override def onEvent(event: Event): Unit = {
    event match {
      case e: PrivateMessageReceivedEvent => onPM(e)
    }
  }

  private def onPM(e: PrivateMessageReceivedEvent): Unit = {
    val attachments = e.getMessage.getAttachments.asScala
    if (attachments.nonEmpty) {
      attachments.find(a => a.getFileName.toLowerCase.endsWith(".xml")).foreach(xmlFile => {
        saveConfig(xmlFile, e.getAuthor)
      })
      // language=RegExp
    } else if (e.getMessage.getContentRaw.toLowerCase.trim.matches("(/config|config\\?)")) {
      sendConfig(e.getAuthor)
    }
  }

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
             .sendMessage("⚠ **Config file is for an unknown guild.**\nMaybe the guildId is wrong?").queue()
       }
    } catch {
      case e: Exception =>
        author.openPrivateChannel().complete()
          .sendMessage(s"⚠ **Could not update that configuration** - ${e.getMessage}\nMaybe config file contains errors?").queue()
    }
  }

  private def sendConfig(author: User): Unit = ???
}
