package de.voteban.config

import java.io.{File, PrintWriter}

import de.voteban.config.XMLConfigurationService.{DEFAULT_CONFIG, guildConfigFile, loadXML, saveXML}
import de.voteban.utils.WithLogger

import scala.xml.{Node, PrettyPrinter}

/**
  * Service for loading and saving configuration files.
  */
class XMLConfigurationService extends WithLogger {
  //TODO Caching

  /**
    * Tries to load the configuration for a guild from a xml file
    *
    * @param guildId the id of the guild that should be loaded
    * @return config of that guild
    * @throws Exception if config loading failed
    */
  def loadGuildConfig(guildId: Long): GuildConfig = {
    val file = guildConfigFile(guildId)
    val config = if (!file.exists()) {
      DEFAULT_CONFIG(guildId)
    } else {
      loadXML(file)
    } \ "config"
    GuildConfig(
      (config \ "guildId").text.toLong
      //Read other config values from config
    )
  }

  /**
    * Tries to save the configuration for a guild to a xml file
    *
    * @param guildConfig the configuration object to save
    * @throws Exception if config saving failed
    */
  def saveGuildConfig(guildConfig: GuildConfig): Unit = {
    val file = guildConfigFile(guildConfig.guildId)
    val config: Node =
      <config>
        <guildId>{guildConfig.guildId}</guildId>
      </config>
    //Save config values to config
    saveXML(config, file)
  }

}

object XMLConfigurationService {

  val CONFIG_DIR = new File("config")

  def DEFAULT_CONFIG(guildId: Long): Node =
    <config>
      <guildId>{guildId}</guildId>
    </config>
  //Add default values for config

  def guildConfigFile(guildId: Long): File = new File(CONFIG_DIR, s"guild-$guildId.xml")

  private def saveXML(xmlContent: Node, dest: File): Unit = {
    val writer = new PrintWriter(dest)
    writer.print(new PrettyPrinter(120, 2).format(xmlContent))
    writer.close()
  }

  private def loadXML(file: File): Node = {
    if (!file.exists()) throw new IllegalArgumentException("")
    val xmlContent = xml.Utility.trim(xml.XML.loadFile(file))
    xmlContent
  }
}