package de.voteban.command
import net.dv8tion.jda.core.entities.Message

object MoastbannedCommand extends Command("moastbanned") {
  /**
    * Override to implement what is done on command
    *
    * @param message message that initiated the command
    */
  override protected def execute(message: Message): Unit = ??? //TODO
}
