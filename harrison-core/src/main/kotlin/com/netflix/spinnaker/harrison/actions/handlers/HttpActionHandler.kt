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
package com.netflix.spinnaker.harrison.actions.handlers

import com.com.netflix.spinnaker.harrison.api.Action
import com.com.netflix.spinnaker.harrison.api.ScheduledAction
import com.netflix.spinnaker.harrison.actions.HttpAction
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.stereotype.Component

@Component
class HttpActionHandler(
  private val okHttpClient: OkHttpClient,
  private val threadPoolExecutor: ThreadPoolTaskExecutor
) : ActionHandler {
  override fun supports(action: Action) = action is HttpAction

  override fun act(scheduledAction: ScheduledAction) {
    val action = scheduledAction.action as HttpAction

    action.toRequest()
      .let { okHttpClient.newCall(it) }
      .also { request ->
        threadPoolExecutor.run { request.execute() }
      }
  }

  private fun HttpAction.toRequest(): Request {
    val request = Request.Builder()
      .method(method, requestBody(body, bodyMediaType))
      .url(url)

    headers.forEach { k, v ->
      request.addHeader(k, v)
    }

    return request.build()
  }

  private fun requestBody(body: String?, bodyContentType: String?): RequestBody? {
    if (body == null || bodyContentType == null) {
      return null
    }
    return RequestBody.create(MediaType.parse(bodyContentType), body)
  }
}
