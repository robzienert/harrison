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
package com.com.netflix.spinnaker.harrison.api

import java.time.Instant
import java.util.*

interface ScheduledAction {
  val id: String
  val schedule: Schedule
  val exclusions: List<Exclusion>
  val action: Action
}

/**
 * Marker interface for Schedule models
 */
interface Schedule {
  val startTime: Instant?
  val endTime: Instant?
}

/**
 * Marker interface for Schedule Exception models
 */
interface Exclusion {
  fun shouldExclude(nextFireTime: Instant): Boolean
}

/**
 * Marker for action models
 */
interface Action

/**
 * External implementation of [ScheduledAction]
 */
data class ScheduledActionApi(
  override val id: String = UUID.randomUUID().toString(),
  override val schedule: Schedule,
  override val exclusions: List<Exclusion> = listOf(),
  override val action: Action
) : ScheduledAction

