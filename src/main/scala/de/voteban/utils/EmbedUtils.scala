package de.voteban.utils

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.entities.{MessageEmbed, User}

class EmbedUtils {

  def getVotebanEmbed(author: User, user: String, amount: Int, reason: String, image: Option[String]): MessageEmbed = {
    val builder = new EmbedBuilder()

    builder.setAuthor(author.getName, "", author.getAvatarUrl)
    builder.setTitle("Voteban")
    builder.setDescription(s"*$user* has been banned **$amount times\n${author.getName} has used /voteban x times")

    builder.addField("Ban Reason", reason, false)
    if(image.isDefined){
      builder.setThumbnail(image.get)
    }

    builder.build()
  }

}
