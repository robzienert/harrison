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

import com.netflix.spinnaker.q.Queue
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration

/**
 * Ensures that the queue has all system-level scheduling messages on the queue.
 */
@Component
class LocalAgent(private val queue: Queue) {

  private val log = LoggerFactory.getLogger(javaClass)

  @Scheduled(fixedDelay = 15_000)
  fun ensureRefreshScheduleMessage() {
    log.debug("Ensuring refresh schedule message exists")
    queue.ensure(RefreshSchedule(), Duration.ofSeconds(30))
  }
}
