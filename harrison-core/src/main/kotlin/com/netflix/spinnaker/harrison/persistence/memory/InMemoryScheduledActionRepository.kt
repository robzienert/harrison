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
package com.netflix.spinnaker.harrison.persistence.memory

import com.netflix.spinnaker.harrison.ScheduledActionImpl
import com.netflix.spinnaker.harrison.TriggerHistoryImpl
import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.persistence.TriggerFireEvent
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

class InMemoryScheduledActionRepository : ScheduledActionRepository {

  private val log = LoggerFactory.getLogger(javaClass)

  private val scheduledActions = ConcurrentHashMap<String, ScheduledActionImpl>()
  private val histories = ConcurrentHashMap<String, MutableList<TriggerFireEvent>>()

  override fun upsert(scheduledAction: ScheduledActionImpl) {
    scheduledActions[scheduledAction.id] = scheduledAction
  }

  override fun delete(id: String) {
    scheduledActions.remove(id)
    histories.remove(id)
  }

  override fun retrieve(id: String): ScheduledActionImpl? {
    return scheduledActions[id]
  }

  override fun retrieveAll(): Iterable<ScheduledActionImpl> {
    return scheduledActions.values
  }

  override fun recordTrigger(id: String, event: TriggerFireEvent) {
    if (!scheduledActions.contains(id)) {
      log.error("Attempted to record trigger event for non-existent scheduled action: $id")
      return
    }
    histories.getOrPut(id, { mutableListOf() }).add(event)
  }

  override fun retrieveHistory(id: String): List<TriggerHistoryImpl> {
    val scheduledAction = scheduledActions[id] ?: return listOf()
    return histories[id]
      ?.map {
        TriggerHistoryImpl(
          scheduledAction = scheduledAction,
          scheduledTime = Instant.ofEpochMilli(it.scheduledTimeEpoch),
          actualTime = Instant.ofEpochMilli(it.actualTimeEpoch)
        )
      }
      ?: listOf()
  }
}
