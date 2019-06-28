package de.voteban.utils

import java.awt.Color
import java.time.OffsetDateTime

import de.voteban.VotebanBot
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.EmbedBuilder.ZERO_WIDTH_SPACE
import net.dv8tion.jda.core.entities.{Member, MessageEmbed}

/**
  * Provides methods for creating preset embeds based on given data
  */
object EmbedUtils {

  /**
    * Color that is used for all embeds
    */
  val COLOR: Color = Color.decode("#32603B")

  /**
    * Shortened URL that links to the bots github page
    */
  val BOT_QUICK_URL: String = "git.io/voteban"

  /**
    * A list of unicode emotes
    *
    * Use `DIGIT_EMOTE(3)` for [[https://discordapp.com/assets/12a9f39a3bfc18e0e4557f60302712a1.svg]]
    */
  val DIGIT_EMOTE: List[String] = List("0⃣", "1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣")

  def votebanEmbed(banned: Member, bannsReceived: Int, author: Member, bannsInitiated: Int, reason: String, isReasonAnImage: Boolean): MessageEmbed = {
    val builder = new EmbedBuilder()
    val msg = s"${banned.getEffectiveName} was banned by ${author.getEffectiveName}"
    builder.setAuthor(msg, null, banned.getUser.getAvatarUrl)
    builder.setDescription(s"**${banned.getEffectiveName}** has been banned ${toEmotes(bannsReceived)} times\n" +
      s"**${author.getEffectiveName}** has banned others ${toEmotes(bannsInitiated)} times")
    builder.setColor(COLOR)
    builder.addField("Ban message:", if (isReasonAnImage) ZERO_WIDTH_SPACE else reason.replace("$user", banned.getEffectiveName), false)
    if (isReasonAnImage) {
      builder.setImage(reason)
    }
    builder.setFooter("git.io/voteban", VotebanBot.JDA.getSelfUser.getEffectiveAvatarUrl)
    builder.setTimestamp(OffsetDateTime.now)
    builder.build()
  }

  /**
    * Embed that is shown when an error occurred
    *
    * @param author user who issued the initial command
    * @param error  the error message
    * @param task   string that specifies when the error happened (optional)
    * @return the created embed
    */
  def errorEmbed(author: Member, error: String, task: String = null): MessageEmbed = {
    val builder = new EmbedBuilder()
    builder.setAuthor("Error",
      null,
      "https://raw.githubusercontent.com/twitter/twemoji/c78b889b61c629ec4506d6d4b5c993d871863d00/72x72/26a0.png")
    builder.setDescription(s"An error occurred while ${Option(task).getOrElse("performing a task")}:\n$error")
    builder.setColor(Color.RED)
    builder.setFooter(BOT_QUICK_URL, VotebanBot.JDA.getSelfUser.getEffectiveAvatarUrl)
    builder.setTimestamp(OffsetDateTime.now)
    builder.build()
  }

  /**
    * Embed that shows of the most often banned users
    *
    * @param leaderList list with the most often banned users and their stats
    * @return the created embed
    */
  def mostBannedEmbed(leaderList: List[(Member, Int)]): MessageEmbed = {
    val builder = new EmbedBuilder()
    builder.setAuthor("Wall of shame")
    builder.setDescription(ZERO_WIDTH_SPACE)
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
      builder.setImage("https://raw.githubusercontent.com/joblo2213/Voteban/master/memes/congrats_banned_kim.jpg")
    }
    builder.setColor(COLOR)
    builder.setFooter(BOT_QUICK_URL, VotebanBot.JDA.getSelfUser.getEffectiveAvatarUrl)
    builder.setTimestamp(OffsetDateTime.now)
    builder.build()
  }

  /**
    * Formats the given number as unicode emote
    *
    * @param i a number
    * @return unicode emote string
    */
  def toEmotes(i: Int): String = i.toString.map[Int](c => c.asDigit).map(DIGIT_EMOTE(_)).mkString

  /**
    * Embed that shows of the users that most often used the /voteban command
    *
    * @param leaderList list with top users of /voteban
    * @return the created embed
    */
  def whoBannedEmbed(leaderList: List[(Member, Int)]): MessageEmbed = {
    val builder = new EmbedBuilder()
    builder.setAuthor("/voteban top users")
    builder.setDescription(ZERO_WIDTH_SPACE)
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
      builder.setImage("https://raw.githubusercontent.com/joblo2213/Voteban/master/memes/everyone_gets_a_ban_operah.jpg")
    }
    builder.setColor(COLOR)
    builder.setFooter(BOT_QUICK_URL, VotebanBot.JDA.getSelfUser.getEffectiveAvatarUrl)
    builder.setTimestamp(OffsetDateTime.now)
    builder.build()
  }

  /**
    * Embed that displays the stats of a user
    *
    * @param user          user for which the stats are displayed
    * @param bannsReceived how often the user was banned
    * @param bansInitiated how often the user banned another user
    * @return the created embed
    */
  def myBannsEmbed(user: Member, bannsReceived: Int, bansInitiated: Int): MessageEmbed = {
    val builder = new EmbedBuilder()
    builder.setAuthor(user.getEffectiveName, null, user.getUser.getAvatarUrl)
    builder.setColor(user.getColorRaw)
    builder.setDescription(ZERO_WIDTH_SPACE)
    builder.addField(s"was banned ${toEmotes(bannsReceived)} times", ZERO_WIDTH_SPACE, false)
    builder.addField(s"banned others ${toEmotes(bansInitiated)} times", ZERO_WIDTH_SPACE, false)
    builder.setFooter(BOT_QUICK_URL, VotebanBot.JDA.getSelfUser.getEffectiveAvatarUrl)
    builder.setTimestamp(OffsetDateTime.now)
    builder.build()
  }
}
