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
package com.netflix.spinnaker.harrison.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.NamedType
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.netflix.spinnaker.harrison.keiko.RefreshSchedule
import com.netflix.spinnaker.harrison.keiko.RunAction
import com.netflix.spinnaker.harrison.keiko.config.KeikoConfiguration
import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.persistence.memory.InMemoryScheduledActionRepository
import com.netflix.spinnaker.q.DeadMessageCallback
import com.netflix.spinnaker.q.Queue
import com.netflix.spinnaker.q.memory.InMemoryQueue
import com.netflix.spinnaker.q.metrics.EventPublisher
import com.netflix.spinnaker.q.metrics.QueueEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import java.time.Clock

@Configuration
@ComponentScan("com.netflix.spinnaker.harrison")
@Import(KeikoConfiguration::class)
open class HarrisonConfiguration {

  @Bean
  @ConditionalOnMissingBean(Clock::class)
  open fun systemClock(): Clock = Clock.systemDefaultZone()

  @Autowired
  open fun objectMapper(mapper: ObjectMapper) {
    mapper.apply {
      registerModule(KotlinModule())
      disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

      registerSubtypes(
        NamedType(RunAction::class.java, "harrison:runAction"),
        NamedType(RefreshSchedule::class.java, "harrison:refreshSchedule")
      )
    }
  }

  @Bean
  @ConditionalOnMissingBean(Queue::class)
  open fun memoryQueue(clock: Clock, deadMessageCallbacks: List<DeadMessageCallback>) = InMemoryQueue(
    clock = clock,
    deadMessageHandlers = deadMessageCallbacks,
    publisher = object : EventPublisher {
      override fun publishEvent(event: QueueEvent) {
        // TODO no-op, but we should be recording metrics on these events
      }
    }
  )

  @Bean
  @ConditionalOnMissingBean(ScheduledActionRepository::class)
  open fun memoryScheduledActionRepository() = InMemoryScheduledActionRepository()
}
