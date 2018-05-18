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
package com.netflix.spinnaker.harrison.server.model

import com.com.netflix.spinnaker.harrison.api.ScheduledAction
import com.com.netflix.spinnaker.harrison.api.ScheduledActionApi
import com.netflix.spinnaker.harrison.ModelConverter
import com.netflix.spinnaker.harrison.ScheduledActionImpl
import org.springframework.stereotype.Component

@Component
class ScheduledActionModelConverter : ModelConverter<ScheduledAction, ScheduledActionImpl, ScheduledActionApi> {

  override fun toExternal(model: ScheduledActionImpl): ScheduledActionApi {
    return model.run {
      ScheduledActionApi(
        id = id,
        schedule = schedule,
        exclusions = exclusions,
        action = action
      )
    }
  }

  override fun toInternal(model: ScheduledActionApi): ScheduledActionImpl {
    return model.run {
      ScheduledActionImpl(
        id = id,
        schedule = schedule,
        exclusions = exclusions,
        action = action
      )
    }
  }
}
