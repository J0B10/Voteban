package de.voteban.command
import net.dv8tion.jda.core.entities.Message

object VotebanCommand extends Command("voteban", Seq("voteban't", "votekick")) {

  override protected def execute(message: Message): Unit = ??? //TODO
}
