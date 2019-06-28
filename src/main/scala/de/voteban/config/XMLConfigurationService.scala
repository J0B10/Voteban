package de.voteban.config

import java.io._
import java.nio.charset.StandardCharsets

import de.voteban.config.XMLConfigurationService.{CONFIG_DIR, DEFAULT_CONFIG, GUILD_CONFIG_FILE_REGEX, guildConfigFile}
import de.voteban.utils.WithLogger

import scala.collection.concurrent.TrieMap
import scala.io.Source
import scala.util.matching.Regex
import scala.xml.{Node, NodeSeq, PrettyPrinter}

/**
  * Service for loading and saving configuration files.
  */
class XMLConfigurationService extends WithLogger {
  //TODO Better Caching

  private val cachedConfigs = TrieMap[Long, GuildConfig]()

  /**
    * Load all configurazion files into cache
    */
  def loadCache(): Unit = {
    if (!XMLConfigurationService.CONFIG_DIR.exists()) CONFIG_DIR.mkdirs()
    CONFIG_DIR.listFiles().foreach(f =>
      f.getName match {
        case GUILD_CONFIG_FILE_REGEX(guildId) =>
          try {
            loadGuildConfig(guildId.toLong)
          } catch {
            case e: Exception =>
              log warn s"Could not load ${f.getName} - ${e.getMessage}"
          }
        case _ => //Ignore default
      }
    )
    log debug "Config files were loaded into cache"
  }

  /**
    * Tries to load the configuration for a guild from a xml file
    *
    * Uses cached configs if available
    *
    * @param guildId the id of the guild that should be loaded
    * @return config of that guild
    * @throws Exception if config loading failed
    */
  @throws(classOf[Exception])
  def loadGuildConfig(guildId: Long): GuildConfig = {
    cachedConfigs.getOrElse(guildId, {
      val file = guildConfigFile(guildId)
      val cfg = if (!file.exists()) {
        DEFAULT_CONFIG(guildId)
      } else {
        this synchronized {
          readGuildConfig(new FileInputStream(file))
        }
      }
      cachedConfigs += (guildId -> cfg)
      log debug s"Cached config for guild $guildId"
      cfg
    })
  }

  /**
    * Tries to read the xml configuration for a guild from an input stream
    *
    * Doesn't cache the loaded configuration
    *
    * @param inputStream the reader providing the xml config file input
    * @return the parsed config object
    * @throws Exception if config loading failed
    */
  @throws(classOf[Exception])
  def readGuildConfig(inputStream: InputStream): GuildConfig = {
    val config = xml.Utility.trim(xml.XML.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) \ "config"
    GuildConfig(
      (config \ "guildId").text.toLong,
      (config \ "banReasons" \ "s").map(_.text),
      (config \ "banReasons" \ "img").map(_.text)
      //Read other config values from config
    )
  }


  /**
    * Tries to save the configuration for a guild to a xml file
    *
    * @param guildConfig the configuration object to save
    */
  def saveGuildConfig(guildConfig: GuildConfig): Unit = {
    val file = guildConfigFile(guildConfig.guildId)
    try {
      this synchronized {
        writeGuildConfig(guildConfig, new FileOutputStream(file, false))
      }
      cachedConfigs += (guildConfig.guildId -> guildConfig)
      log debug s"Saved config ${file.getName}"
    } catch {
      case e: Exception => log warn s"Failed to save ${file.getName} - ${e.getMessage}"
    }
  }

  /**
    * Tries to write the configuration for a guild to an output stream
    *
    * @param guildConfig  the configuration object to save
    * @param outputStream the output strem to which the configuration should be written
    * @throws Exception if config saving failed
    */
  @throws(classOf[Exception])
  def writeGuildConfig(guildConfig: GuildConfig, outputStream: OutputStream): Unit = {
    val writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))
    val config: Node =
      <config>
        <guildId>
          {guildConfig.guildId}
        </guildId>
        <banReasons>
          {NodeSeq.fromSeq(guildConfig.banReasons.map(s => <s>
          {s}
        </s>))}{NodeSeq.fromSeq(guildConfig.banReasonImages.map(img => <img>
          {img}
        </img>))}
        </banReasons>
      </config>
    //Save config values to config
    writer.print(new PrettyPrinter(120, 2).format(config))
    writer.close()
  }

}

object XMLConfigurationService {

  // language=RegExp
  val GUILD_CONFIG_FILE_REGEX: Regex = "guild-(\\d+).xml".r
  val CONFIG_DIR = new File("config")

  private val BAN_REASONS_DEFAULT =
    Source.fromInputStream(getClass.getResourceAsStream("/ban_reasons.txt"), "UTF8")
    .getLines.filter(line => !line.startsWith("#") && !line.isEmpty).toSeq

  private val BAN_REASON_IMAGES_DEFAULT =
    Source.fromInputStream(getClass.getResourceAsStream("/ban_reasons.images.txt"), "UTF8")
    .getLines.filter(line => !line.startsWith("#") && !line.isEmpty).toSeq

  def DEFAULT_CONFIG(guildId: Long): GuildConfig = GuildConfig(
    guildId,
    BAN_REASONS_DEFAULT,
    BAN_REASON_IMAGES_DEFAULT

    //Add default values
  )

  def guildConfigFile(guildId: Long): File = new File(CONFIG_DIR, s"guild-$guildId.xml")
}