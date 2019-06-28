package de.voteban.db

import java.io.{File, FileOutputStream, OutputStreamWriter, PrintWriter}
import java.nio.charset.StandardCharsets

import de.voteban.db.JSONDatabaseService.DATABASE_FILE
import de.voteban.utils.WithLogger
import org.json4s.native.Serialization
import org.json4s.{Formats, NoTypeHints}

import scala.io.Source

/**
  * Service for saving all data into a big json file
  */
class JSONDatabaseService extends WithLogger {
  //TODO Real Database using MongoDB or an sql based database (maybe sqlite?)

  implicit val formats: AnyRef with Formats = Serialization.formats(NoTypeHints)

  @volatile
  private var _database: Option[Database] = None

  /**
    * Loads all data into the database
    */
  def loadDatabase(): Unit = {
    if (!DATABASE_FILE.exists()) {
      val w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(DATABASE_FILE), StandardCharsets.UTF_8))
      w.print(Serialization.write(Database(Map())))
      w.close()
    }
    _database = Some(Serialization.read[Database](Source.fromFile(DATABASE_FILE, "UTF8").mkString))
    log debug "Database loaded"
  }

  /**
    * Modifies the data of an user
    *
    * Blocking if another thread is currently writing to the database
    *
    * @param userData user data to write to the database
    * @param guildId  id of the guild to which this user data belongs
    * @throws java.lang.IllegalStateException if the database wasn't loaded yet
    */
  @throws(classOf[IllegalStateException])
  def updateUserData(userData: UserData, guildId: Long): Unit = {
    this synchronized {
      val guild = database.guilds.getOrElse(guildId, GuildData(guildId, Map()))
      _database = Some(
        Database(database.guilds + (guildId -> GuildData(guildId, guild.users + (userData.userId -> userData))))
      )
      saveDatabase()
    }
  }

  /**
    * Add a guild with no user data to the database
    *
    * @param guildId id of the guild
    * @throws IllegalStateException if the database wasn't loaded yet
    */
  @throws(classOf[IllegalStateException])
  def addGuild(guildId: Long): Unit = {
    this synchronized {
      _database = Some(Database(database.guilds + (guildId -> GuildData(guildId, Map()))))
      saveDatabase()
    }
  }

  /**
    * @return The database object that provides all data
    * @throws IllegalStateException if the database wasn't loaded yet
    */
  @throws(classOf[IllegalStateException])
  def database: Database = _database.getOrElse(throw new IllegalStateException("Database not loaded yet"))

  private def saveDatabase(): Unit = {
    val w = new PrintWriter(new OutputStreamWriter(new FileOutputStream(DATABASE_FILE), StandardCharsets.UTF_8))
    w.print(Serialization.write(database))
    w.close()
    log debug "Database saved"
  }

}

object JSONDatabaseService {
  val DATABASE_FILE = new File("db.json")
}
