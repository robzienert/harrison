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
package com.netflix.spinnaker.harrison.actions

import com.com.netflix.spinnaker.harrison.api.Action
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody

/**
 * TODO rz - Add retry controls
 * TODO rz - Should this go into the api module?
 */
data class HttpAction(
  val method: String,
  val url: String,
  val body: String? = null,
  val bodyMediaType: String? = null,
  val headers: Map<String, String> = mapOf()
) : Action {

  internal fun toRequest(): Request {
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
