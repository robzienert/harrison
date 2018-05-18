package com.netflix.spinnaker.harrison.scheduling

import com.com.netflix.spinnaker.harrison.api.Schedule
import com.com.netflix.spinnaker.harrison.api.ScheduledAction
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import com.cronutils.parser.CronParser
import com.netflix.spinnaker.harrison.scheduling.schedules.CronSchedule
import org.springframework.stereotype.Component
import java.time.Clock

/**
 * TODO rz - Add cron fuzzing support
 */
@Component
class CronScheduler(
  private val clock: Clock
) : Scheduler {

  private val parser = CronParser(CronDefinitionBuilder.instanceDefinitionFor(CronType.UNIX))

  override fun supports(schedule: Schedule) = schedule is CronSchedule

  override fun nextTrigger(action: ScheduledAction): ScheduleTrigger? {
    if (outsideWindow(action.schedule)) {
      // TODO log
      return null
    }

    return (action.schedule as CronSchedule)
      .nextExecution(parser)
      ?.let {
        ScheduleTrigger(it.toInstant())
      }
  }

  private fun outsideWindow(schedule: Schedule): Boolean {
    if (schedule.startTime != null && clock.instant().isBefore(schedule.startTime)) {
      return true
    }
    if (schedule.endTime != null && clock.instant().isAfter(schedule.endTime)) {
      return true
    }
    return false
  }
}
