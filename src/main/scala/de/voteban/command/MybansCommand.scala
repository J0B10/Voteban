package de.voteban.command

import de.voteban.VotebanBot
import de.voteban.utils.EmbedUtils
import net.dv8tion.jda.core.entities.Message

object MybansCommand extends Command("mybans") {

  override protected def execute(message: Message): Unit = {
    val usrData = VotebanBot.USER_DATA(message.getMember)
    message.getChannel.sendMessage(EmbedUtils.myBannsEmbed(message.getMember, usrData.bansReceived, usrData.bansInitiated)).queue()
  }
}
