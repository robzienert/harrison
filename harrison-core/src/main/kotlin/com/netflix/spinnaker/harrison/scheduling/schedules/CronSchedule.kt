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
package com.netflix.spinnaker.harrison.scheduling.schedules

import com.com.netflix.spinnaker.harrison.api.Schedule
import com.cronutils.model.time.ExecutionTime
import com.cronutils.parser.CronParser
import java.time.Instant
import java.time.ZonedDateTime

data class CronSchedule(
  override val startTime: Instant?,
  override val endTime: Instant?,
  val expression: String
) : Schedule {

  fun nextExecution(parser: CronParser): ZonedDateTime? =
    ExecutionTime.forCron(parser.parse(expression)).nextExecution(ZonedDateTime.now())
      .let {
        if (it.isPresent) {
          it.get()
        }
        null
      }
}

