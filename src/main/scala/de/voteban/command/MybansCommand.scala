package de.voteban.command

import de.voteban.VotebanBot
import de.voteban.db.UserData
import de.voteban.utils.EmbedUtils
import net.dv8tion.jda.core.entities.Message

object MybansCommand extends Command("mybans", Seq()) {

  override protected def execute(message: Message): Unit = {
    val usrData = VotebanBot.GUILD_DATA(message.getGuild).users
      .getOrElse(message.getAuthor.getIdLong, UserData(message.getAuthor.getIdLong, 0, 0))
    message.getChannel.sendMessage(EmbedUtils.myBannsEmbed(message.getMember, usrData.bansReceived, usrData.bansInitiated)).queue()
  }
}
