# harrison

An extensible, distributed scheduled actions library & service.

* Actions (or callbacks) are pluggable; HTTP & SQS actions built-in
* Scheduling logic is pluggable; cron and windows built-in
* Scheduling exceptions logic is pluggable; Weekend calendar built-in
* Persistence backend is pluggable; in-memory and Spinnaker's front50 built-in

Harrison is built primarily as a [Keiko](1) extension, so it's first and foremost a library, but it ships with an optional web service container as well.

**NOTE ON PLUGINS**: Modules with external service dependencies (aside from Keiko) are all optional. Using Harrison does not force you into the built-in dependencies if you want to add new plugins.

## Usage as a Library

_TODO_

## Usage as a Service

_TODO_

## Relationship with Spinnaker

Harrison can (and should!) be used totally separately from [Spinnaker](2). Aside from using front50 as an persistence option, there are no integrations.

## Why "Harrison"

Named after John Harrison, inventor of the marine chronometer. It's important to have a nautical themed name.

[1]: https://github.com/spinnaker/keiko
[2]: https://github.com/spinnaker
