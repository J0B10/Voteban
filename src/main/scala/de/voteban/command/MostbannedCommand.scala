package de.voteban.command
import de.voteban.VotebanBot
import de.voteban.db.UserData
import de.voteban.utils.EmbedUtils
import net.dv8tion.jda.core.entities.Message

object MostbannedCommand extends Command("mostbanned") {
  /**
    * Override to implement what is done on command
    *
    * @param message message that initiated the command
    */
  override protected def execute(message: Message): Unit = {
    val sorted = VotebanBot.GUILD_DATA(message.getGuild).users.values.toList.sortWith(sortByBanns)
    val leaders = sorted.slice(0, if (sorted.length < 10) sorted.length else 10).map(userData => {
      (message.getGuild.getMemberById(userData.userId), userData.bansReceived)
    })
    message.getChannel.sendMessage(EmbedUtils.mostBannedEmbed(leaders)).queue()
  }

  private def sortByBanns(one: UserData, other: UserData) = {
    one.bansReceived > other.bansReceived
  }
}
