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
package com.netflix.spinnaker.harrison.persistence

import com.netflix.spinnaker.harrison.ScheduledActionImpl
import com.netflix.spinnaker.harrison.TriggerHistoryImpl

interface ScheduledActionRepository {

  fun upsert(scheduledAction: ScheduledActionImpl)
  fun delete(id: String)
  fun retrieve(id: String): ScheduledActionImpl?
  fun retrieveAll(): Iterable<ScheduledActionImpl>
  fun recordTrigger(id: String, event: TriggerFireEvent)
  fun retrieveHistory(id: String): List<TriggerHistoryImpl>
}

data class TriggerFireEvent(
  val scheduledTimeEpoch: Long,
  val actualTimeEpoch: Long
)
