package de.voteban.command

import de.voteban.utils.EmbedUtils
import net.dv8tion.jda.core.entities.{Message, MessageType}

object VotebanCommand extends Command("voteban", Seq("voteban't", "votekick")) {

  override protected def execute(message: Message): Unit = {
    println("test")
    println(message.getType)
    if (!message.getTextChannel.canTalk) {
      println("Can't talk in here")
      return
    }
    if (message.getType == MessageType.DEFAULT) {
      if (!message.getContentRaw.contains(" ")) {
        message.getChannel.sendMessage(EmbedUtils.errorEmbed(message.getAuthor, "You have to use the voteban command with at least another argument")).complete()
        return
      }
      val messageParts = message.getContentDisplay.split(" ")
      message.getChannel.sendMessage(EmbedUtils.votebanEmbed(message.getAuthor, messageParts(1), 5, "test", None)).complete()
    }
  } //TODO
}
