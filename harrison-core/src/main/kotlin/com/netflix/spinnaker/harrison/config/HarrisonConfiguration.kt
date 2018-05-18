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
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.netflix.spinnaker.config.ObjectMapperSubtypeProperties
import com.netflix.spinnaker.config.SpringObjectMapperConfigurer
import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.persistence.memory.InMemoryScheduledActionRepository
import com.netflix.spinnaker.q.DeadMessageCallback
import com.netflix.spinnaker.q.Queue
import com.netflix.spinnaker.q.memory.InMemoryQueue
import com.netflix.spinnaker.q.metrics.EventPublisher
import com.netflix.spinnaker.q.metrics.QueueEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
@ComponentScan("com.netflix.spinnaker.harrison")
@EnableConfigurationProperties(ObjectMapperSubtypeProperties::class)
open class HarrisonConfiguration {

  @Bean
  @ConditionalOnMissingBean(Clock::class)
  open fun systemClock(): Clock = Clock.systemDefaultZone()

  @Autowired
  open fun objectMapper(mapper: ObjectMapper,
                   objectMapperSubtypeProperties: ObjectMapperSubtypeProperties) {
    mapper.apply {
      registerModule(KotlinModule())
      disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

      SpringObjectMapperConfigurer(objectMapperSubtypeProperties.apply {
        messagePackages += listOf("com.netflix.spinnaker.harrison.keiko")
        attributePackages += listOf("com.netflix.spinnaker.harrison.keiko")
      }).registerSubtypes(this)
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
