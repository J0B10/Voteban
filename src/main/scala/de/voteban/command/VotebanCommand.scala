package de.voteban.command

import de.voteban.VotebanBot
import de.voteban.db.UserData
import de.voteban.utils.EmbedUtils
import net.dv8tion.jda.core.entities.{Member, Message, TextChannel}

import scala.jdk.CollectionConverters._
import scala.util.Random

object VotebanCommand extends Command("voteban", Seq("votekick", "ban")) {

  // language=RegExp
  private val COMMAND_REGEX =
    """/\w+ ([^\s]+)?(.+)?""".r

  override protected def execute(message: Message): Unit = {
    message.getContentRaw.trim match {
      case COMMAND_REGEX(banned, reason) =>
        parseUser(banned, message).foreach(user => voteban(user, message.getMember, message.getTextChannel, reason))
      case COMMAND_REGEX(banned) =>
        parseUser(banned, message).foreach(user => voteban(user, message.getMember, message.getTextChannel))
      case _ =>
        message.getChannel.sendMessage(EmbedUtils.errorEmbed(message.getMember, "Please specify the name of the user you want to voteban.", "handling your command")).queue()
    }
  }

  private def parseUser(name: String, message: Message): Option[Member] = {
    val small = name.trim.toLowerCase
    message.getMentionedUsers.asScala.find(_.getAsMention == name.trim).map(message.getGuild.getMember(_)).filter(Option(_).isDefined).orElse({
      val guildMembers: List[Member] = message.getGuild.getMembers.asScala.toList
      guildMembers.find(_.getUser.getId == name.trim).orElse({
        val possibleUsers: List[Member] = (guildMembers.filter(_.getUser.getName.toLowerCase.contains(small))
          ++ guildMembers.filter(usr => Option(usr.getNickname).exists(_.toLowerCase.contains(small))))
        if (possibleUsers.isEmpty) {
          message.getChannel.sendMessage(EmbedUtils.errorEmbed(
            message.getMember,
            s"Couldn't find a user that matched `$name`.",
            "handling your command")).queue()
          None
        } else if (possibleUsers.length == 1) {
          Some(possibleUsers(0))
        } else if (possibleUsers.length < 10) {
          message.getChannel.sendMessage(EmbedUtils.errorEmbed(
            message.getMember,
            "Couldn't identify that user, please mention him or use his id.\nMaybe he is one of these:\n\n"
              + possibleUsers.map(m => s"${m.getEffectiveName} _(id:`${m.getUser.getId}`)_").mkString("\n"),
            "handling your command"
          )).queue()
          None
        } else {
          message.getChannel.sendMessage(EmbedUtils.errorEmbed(
            message.getMember, "Couldn't identify that user, please mention him or use his id.", "handling your command")).queue()
          None
        }
      })
    })
  }

  private def voteban(banned: Member, author: Member, channel: TextChannel, reason: String = null): Unit = {
    var bannedData = VotebanBot.USER_DATA(banned)
    var authorData = VotebanBot.USER_DATA(author)
    bannedData = UserData(bannedData.userId, bannedData.bansReceived + 1, bannedData.bansInitiated)
    authorData = UserData(authorData.userId, authorData.bansReceived, authorData.bansInitiated + 1)
    VotebanBot.databaseService.updateUserData(bannedData, banned.getGuild.getIdLong)
    VotebanBot.databaseService.updateUserData(authorData, author.getGuild.getIdLong)
    Option(reason) match {
      case Some(r) =>
        channel.sendMessage(EmbedUtils.votebanEmbed(banned, bannedData.bansReceived, author, authorData.bansInitiated, reason, isReasonAnImage = false)).queue()
      case _ =>
        val reasons = VotebanBot.GUILD_CONFIG(channel.getGuild).banReasons
        val reasonImages = VotebanBot.GUILD_CONFIG(channel.getGuild).banReasonImages
        val random = Random.nextInt(reasons.length + reasonImages.length)
        if (random < reasons.length) {
          channel.sendMessage(EmbedUtils.votebanEmbed(banned, bannedData.bansReceived, author, authorData.bansInitiated, reasons(random), isReasonAnImage = false)).queue()
        } else {
          channel.sendMessage(EmbedUtils.votebanEmbed(banned, bannedData.bansReceived, author, authorData.bansInitiated, reasonImages(random - reasons.length), isReasonAnImage = true)).queue()
        }
    }
  }
}
