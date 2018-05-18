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
package com.netflix.spinnaker.harrison.model

import com.com.netflix.spinnaker.harrison.api.TriggerHistory
import com.com.netflix.spinnaker.harrison.api.TriggerHistoryApi
import com.netflix.spinnaker.harrison.ModelConverter
import com.netflix.spinnaker.harrison.TriggerHistoryImpl
import org.springframework.stereotype.Component

@Component
class TriggerHistoryModelConverter : ModelConverter<TriggerHistory, TriggerHistoryImpl, TriggerHistoryApi> {

  override fun toExternal(model: TriggerHistoryImpl): TriggerHistoryApi {
    return model.run {
      TriggerHistoryApi(
        scheduledAction = scheduledAction,
        scheduledTime = scheduledTime,
        actualTime = actualTime
      )
    }
  }

  override fun toInternal(model: TriggerHistoryApi): TriggerHistoryImpl {
    return model.run {
      TriggerHistoryImpl(
        scheduledAction = scheduledAction,
        scheduledTime = scheduledTime,
        actualTime = actualTime
      )
    }
  }
}
