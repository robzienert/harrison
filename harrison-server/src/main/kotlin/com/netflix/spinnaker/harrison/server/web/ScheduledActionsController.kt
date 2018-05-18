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
package com.netflix.spinnaker.harrison.server.web

import com.com.netflix.spinnaker.harrison.api.RequestWrapper
import com.com.netflix.spinnaker.harrison.api.ResponseWrapper
import com.com.netflix.spinnaker.harrison.api.ScheduledActionApi
import com.com.netflix.spinnaker.harrison.api.TriggerHistory
import com.netflix.spinnaker.harrison.persistence.ScheduledActionRepository
import com.netflix.spinnaker.harrison.model.ScheduledActionModelConverter
import com.netflix.spinnaker.harrison.model.TriggerHistoryModelConverter
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.*
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/scheduledActions")
class ScheduledActionsController(
  private val repository: ScheduledActionRepository,
  private val scheduledActionConverter: ScheduledActionModelConverter,
  private val triggerHistoryConverter: TriggerHistoryModelConverter
) {

  @RequestMapping(value = [""], method = [GET])
  fun list(): ResponseWrapper<List<ScheduledActionApi>> {
    return ResponseWrapper(
      repository.retrieveAll().map { scheduledActionConverter.toExternal(it) }
    )
  }

  @RequestMapping(value = ["/{id}"], method = [GET])
  fun history(@RequestParam("id") id: String): ResponseWrapper<List<TriggerHistory>> {
    return ResponseWrapper(
      repository.retrieveHistory(id).map { triggerHistoryConverter.toExternal(it) }
    )
  }

  @RequestMapping(value = ["/{id}"], method = [POST])
  fun upsert(@RequestBody scheduledAction: RequestWrapper<ScheduledActionApi>) {
    repository.upsert(scheduledActionConverter.toInternal(scheduledAction.payload))
  }

  @RequestMapping(value = ["/{id}"], method = [DELETE])
  fun delete(id: String) {
    repository.delete(id)
  }
}
