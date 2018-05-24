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

import com.netflix.spinnaker.harrison.ScheduledActionImpl
import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.scheduling.ScheduleTrigger
import com.netflix.spinnaker.harrison.scheduling.Scheduler
import com.netflix.spinnaker.harrison.scheduling.SchedulerProvider
import com.netflix.spinnaker.harrison.scheduling.schedules.SimpleSchedule
import com.netflix.spinnaker.harrison.test.NoopAction
import com.netflix.spinnaker.q.Queue
import com.nhaarman.mockito_kotlin.*
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

object RefreshScheduleHandlerTest : SubjectSpek<RefreshScheduleHandler>({

  val queue = mock<Queue>()
  val repository = mock<ScheduledActionRepository>()
  val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
  val scheduler = mock<Scheduler>()

  subject {
    RefreshScheduleHandler(queue, repository, SchedulerProvider(listOf(scheduler)), clock, 15_000)
  }

  fun resetMocks() {
    reset(queue, scheduler, repository)
  }

  given("no schedules in system") {
    beforeGroup {
      whenever(repository.retrieveAll()) doReturn listOf<ScheduledActionImpl>()
    }

    afterGroup { resetMocks() }

    on("refreshing schedule") {
      subject.handle(RefreshSchedule())

      it("schedules a new refresh message") {
        verify(queue, times(1)).ensure(isA<RefreshSchedule>(), any())
        verifyNoMoreInteractions(queue)
      }
    }
  }

  given("a valid schedule") {
    val scheduledAction = ScheduledActionImpl(
      id = "1",
      schedule = SimpleSchedule.once(clock.instant()),
      action = NoopAction()
    )

    beforeGroup {
      whenever(repository.retrieveAll()) doReturn listOf(scheduledAction)
      whenever(scheduler.supports(any())) doReturn true
      whenever(scheduler.nextTrigger(eq(scheduledAction))) doReturn ScheduleTrigger(deliverAt = clock.instant())
    }

    afterGroup { resetMocks() }

    on("refreshing schedule") {
      subject.handle(RefreshSchedule())

      it("schedules the action") {
        verify(queue, times(1)).ensure(isA<RunAction>(), eq(Duration.ZERO))
      }

      it("reschedules itself") {
        verify(queue, times(1)).ensure(isA<RefreshSchedule>(), any())
        verifyNoMoreInteractions(queue)
      }
    }
  }
})
