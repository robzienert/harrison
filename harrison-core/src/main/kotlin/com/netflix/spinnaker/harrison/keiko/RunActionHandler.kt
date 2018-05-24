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

import com.netflix.spinnaker.harrison.actions.handlers.ActionHandlerProvider
import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.persistence.TriggerFireEvent
import com.netflix.spinnaker.q.MessageHandler
import com.netflix.spinnaker.q.Queue
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component
import java.time.Clock

@Component
class RunActionHandler(
  override val queue: Queue,
  private val repository: ScheduledActionRepository,
  private val actionHandlerProvider: ActionHandlerProvider,
  private val threadPool: ThreadPoolTaskExecutor,
  private val clock: Clock
) : MessageHandler<RunAction> {

  override val messageType = RunAction::class.java

  override fun handle(message: RunAction) {
    repository.retrieve(message.id)
      ?.let {
        threadPool.run {
          actionHandlerProvider.provide(it.action).act(it)
          repository.recordTrigger(
            it.id,
            TriggerFireEvent(
              message.scheduledTimeEpochMs,
              clock.millis()
            )
          )
        }
      }
  }
}
