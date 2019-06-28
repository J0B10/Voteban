package de.voteban.utils

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.Executors

import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps

/**
  * Schedules a shutdown of the bot after a given time interval
  *
  * @param scheduleIn     duration after which the bot should be shut down
  * @param notifyInterval interval between the notifications before shutdown
  * @param notifyTimes    how often should be notified before shutdown (0 to deactivate notifications)
  */
class RestartScheduler(scheduleIn: FiniteDuration, val notifyInterval: FiniteDuration, val notifyTimes: Int) extends WithLogger {

  /**
    * Time when the restart scheduler was setup
    */
  val setup_time: OffsetDateTime = OffsetDateTime.now
  /**
    * Time at which the next restart will be performed
    */
  val restart_time: OffsetDateTime = OffsetDateTime.now.plus(scheduleIn.toMillis, ChronoUnit.MILLIS)
  private val executor = Executors.newSingleThreadScheduledExecutor()
  private val shutdown: Runnable = () => {
    log info "SHUTTING DOWN NOW!"
    sys exit 0
  }
  private val countdown: Runnable = () => {
    val left = (notifyTimes - i) * notifyInterval
    i += 1
    log info s"Restart scheduled in $left"
  }
  private var i = 0

  /**
    * Start this restart scheduler
    */
  def init(): Unit = {
    log info s"Restart scheduled at ${restart_time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)}"
    executor.scheduleWithFixedDelay(countdown, (scheduleIn - notifyInterval * notifyTimes).toMillis, notifyInterval.toMillis, MILLISECONDS)
    executor.schedule(shutdown, scheduleIn.toMillis, MILLISECONDS)
  }

  /**
    * Stops this scheduler and cancels all actions
    */
  def cancel(): Unit = {
    executor.shutdownNow()
  }
}

object RestartScheduler {

  /**
    * Schedules a shutdown of the bot after a given time interval
    *
    * @param scheduleIn     duration after which the bot should be shut down
    * @param notifyInterval interval between the notifications before shutdown
    * @param notifyTimes    how often should be notified before shutdown (0 to deactivate notifications)
    */
  def apply(scheduleIn: FiniteDuration, notifyInterval: FiniteDuration, notifyTimes: Int): RestartScheduler =
    new RestartScheduler(scheduleIn, notifyInterval, notifyTimes)

  /**
    * Schedules a shutdown of the bot after a given time interval<br>
    * Won't display any notifications before shutdown
    *
    * @param scheduleIn duration after which the bot should be shut down
    */
  def apply(scheduleIn: FiniteDuration): RestartScheduler =
    new RestartScheduler(scheduleIn, 0 seconds, 0)

  /**
    * Schedules a shutdown of the bot at a given time
    *
    * @param scheduleAt     time at which the bot should shut down
    * @param notifyInterval interval between the notifications before shutdown
    * @param notifyTimes    how often should be notified before shutdown (0 to deactivate notifications)
    */
  def apply(scheduleAt: OffsetDateTime, notifyInterval: FiniteDuration, notifyTimes: Int) =
    new RestartScheduler(OffsetDateTime.now().until(scheduleAt, ChronoUnit.MILLIS) milliseconds, notifyInterval, notifyTimes)

}