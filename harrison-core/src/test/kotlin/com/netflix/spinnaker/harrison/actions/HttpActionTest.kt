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

import okhttp3.Request
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import strikt.api.expect
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

object HttpActionTest : Spek({

  given("a http action") {
    val action = HttpAction(
      method = "POST",
      url = "https://localhost/things",
      body = "{\"hello\":\"world\"}",
      bodyMediaType = "application/json",
      headers = mapOf(
        "Accept" to "application/json"
      )
    )

    on("convert to Request") {
      val request = action.toRequest()

      it("creates a Request matching the definition") {
        expect(request) {
          map(Request::method).isEqualTo("POST")
          map(Request::body).isNotNull()
          map(Request::isHttps).isEqualTo(true)
          map{ header("Accept") }.isEqualTo("application/json")
        }
      }
    }
  }
})
