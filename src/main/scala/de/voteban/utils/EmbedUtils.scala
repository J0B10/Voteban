package de.voteban.utils

import java.awt.Color

import de.voteban.VotebanBot
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.EmbedBuilder.ZERO_WIDTH_SPACE
import net.dv8tion.jda.core.entities.{Member, MessageEmbed, User}

/**
  * Provides methods for creating preset embeds based on given data
  */
object EmbedUtils {

  /**
    * Color that is used for all embeds
    */
  val COLOR: Color = Color.decode("#32603B")

  /**
    * A list of unicode emotes
    *
    * Use `DIGIT_EMOTE(3)` for [[https://discordapp.com/assets/12a9f39a3bfc18e0e4557f60302712a1.svg]]
    */
  val DIGIT_EMOTE: List[String] = List("0⃣ ", "1⃣ ", "2⃣ ", "3⃣ ", "4⃣ ", "5⃣ ", "6⃣ ", "7⃣ ", "8⃣ ", "9⃣ ")

  //TODO derNiklaas continue
  def votebanEmbed(author: User, user: String, amount: Int, reason: String, image: Option[String]): MessageEmbed = {
    val builder = new EmbedBuilder()

    builder.setAuthor(author.getName, null, author.getAvatarUrl)
    builder.setTitle("Voteban")
    builder.setDescription(s"*$user* has been banned **$amount times**\n${author.getName} has used /voteban x times")
    builder.setColor(Color.GREEN)

    builder.addField("Ban Reason", reason, false)
    if (image.isDefined) {
      builder.setImage(image.get)
    }

    builder.build()
  }

  def errorEmbed(author: User, error: String): MessageEmbed = {
    val builder = new EmbedBuilder()

    builder.setAuthor(author.getName, null, author.getAvatarUrl)
    builder.setTitle("Error")
    builder.setDescription(s"An error occurred while performing a task:\n$error")
    builder.setColor(Color.RED)
    builder.build()
  }

  /**
    * Embed that shows of the most often banned users
    *
    * @param leaderList list with the most often banned users and their stats
    * @return
    */
  def mostBannedEmbed(leaderList: List[(Member, Int)]): MessageEmbed = {
    val builder = new EmbedBuilder()
    builder.setAuthor("Wall of shame", null, VotebanBot.JDA.getSelfUser.getEffectiveAvatarUrl)
    for (((user, value), i) <- leaderList.zipWithIndex) {
      builder.addField(
        s"${toEmotes(i + 1)} **${user.getEffectiveName}** _(${user.getUser.getAsTag})_ banned **$value** times",
        ZERO_WIDTH_SPACE,
        false
      )
    }
    if (builder.getFields.isEmpty) {
      builder.setDescription("No users banned yet.\nBe the first who banned someone, use `/votban`!")
    } else {
      builder.setImage("https://raw.githubusercontent.com/joblo2213/Voteban-t/memes/congrats_banned_kim.jpg")
    }
    builder.setColor(COLOR)
    builder.build()
  }

  /**
    * Formats the given number as unicode emote
    *
    * @param i a number
    * @return unicode emote string
    */
  def toEmotes(i: Int): String = i.toString.map[Int](c => c.asDigit).map(DIGIT_EMOTE(_)).mkString
}
