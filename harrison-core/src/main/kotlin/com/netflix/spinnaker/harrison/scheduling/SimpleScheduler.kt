/*
 * Copyright 2018 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.spinnaker.harrison.scheduling

import com.com.netflix.spinnaker.harrison.api.Exclusion
import com.com.netflix.spinnaker.harrison.api.Schedule
import com.com.netflix.spinnaker.harrison.api.ScheduledAction
import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.scheduling.schedules.SimpleSchedule
import java.time.Clock
import java.time.Duration
import java.time.Instant

private const val MAX_LOOKAHEAD_STEPS = 10

class SimpleScheduler(
  private val repository: ScheduledActionRepository,
  private val clock: Clock
) : Scheduler {

  override fun supports(schedule: Schedule) = schedule is SimpleSchedule

  override fun nextTrigger(action: ScheduledAction): ScheduleTrigger? {
    val schedule = action.schedule as SimpleSchedule

    val now = clock.instant()

    schedule.endTime?.run {
      if (isBefore(now)) {
        return null
      }
    }

    schedule.startTime?.run {
      if (isAfter(now)) {
        return null
      }
    }

    val lastTrigger = repository.retrieveLatestTrigger(action.id) ?: return findNextTriggerTime(
      sourceTime = null,
      delay = schedule.fixedDelay,
      exclusions = action.exclusions
    )

    if (schedule.maxTimes != null && lastTrigger.counter >= schedule.maxTimes) {
      return null
    }

    return findNextTriggerTime(
      sourceTime = lastTrigger.scheduledTime,
      delay = schedule.fixedDelay,
      exclusions = action.exclusions
    )
  }

  private tailrec fun findNextTriggerTime(sourceTime: Instant?,
                                          delay: Duration,
                                          exclusions: List<Exclusion>,
                                          counter: Int = 0): ScheduleTrigger? {
    if (counter > MAX_LOOKAHEAD_STEPS) {
      return null
    }

    val candidateTime = (sourceTime ?: clock.instant()).plus(delay)
    if (exclusions.none { it.shouldExclude(candidateTime) }) {
      return ScheduleTrigger(deliverAt = candidateTime)
    }

    return findNextTriggerTime(candidateTime, delay, exclusions, counter + 1)
  }
}
