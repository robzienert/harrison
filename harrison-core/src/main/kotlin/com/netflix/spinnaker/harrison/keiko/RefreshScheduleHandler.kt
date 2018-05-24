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
package com.netflix.spinnaker.harrison.keiko

import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.scheduling.SchedulerNotFound
import com.netflix.spinnaker.harrison.scheduling.SchedulerProvider
import com.netflix.spinnaker.q.MessageHandler
import com.netflix.spinnaker.q.Queue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import javax.annotation.PostConstruct

@Component
class RefreshScheduleHandler(
  override val queue: Queue,
  private val repository: ScheduledActionRepository,
  private val schedulerProvider: SchedulerProvider,
  private val clock: Clock,
  @Value("\${harrison.handlers.refreshSchedule.intervalMs:15000}") private val refreshIntervalMs: Long
) : MessageHandler<RefreshSchedule> {

  private val log = LoggerFactory.getLogger(javaClass)

  override val messageType = RefreshSchedule::class.java

  @PostConstruct
  fun init() {
    queue.push(RefreshSchedule())
  }

  override fun handle(message: RefreshSchedule) {
    repository.retrieveAll()
      .forEach { action ->
        try {
          schedulerProvider
            .provide(action.schedule)
            .nextTrigger(action)
            ?.deliverAt
            ?.also {
              queue.ensure(
                RunAction(action.id, it.toEpochMilli()),
                Duration.ofMillis(it.toEpochMilli() - clock.millis())
              )
            }
        } catch (e: SchedulerNotFound) {
          // TODO handle
          log.error("$e")
        }
      }
    queue.ensure(RefreshSchedule(), Duration.ofMillis(refreshIntervalMs))
  }
}
