# harrison

An extensible, distributed scheduled actions library & service.

* Actions (or callbacks) are pluggable; HTTP & SQS actions built-in
* Scheduling logic is pluggable; cron, limits and windows built-in
* Scheduling exceptions logic is pluggable; US Holiday calendar built-in
* Persistence backend is pluggable; in-memory and Spinnaker's front50 built-in
* Coordination backend is pluggable; Redis and ZooKeeper built-in

Harrison is built primarily as a [Keiko](1) extension, so it's first and foremost a library, but it ships with an optional web service container as well.

**NOTE ON PLUGINS**: Modules with external dependencies (aside from Keiko) are all optional. Using harrison does not force you into the built-in dependencies if you want to add new plugins.

## Usage as a Library

_TODO_

## Usage as a Service

_TODO_

## Relationship with Spinnaker

Harrison can (and should!) be used totally separately from Spinnaker. Aside from using front50 as an persistence option, there are no integrations.

## Why Harrison

Named after John Harrison, inventor of the marine chronometer.
