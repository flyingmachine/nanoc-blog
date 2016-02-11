---
title: Composing Strings with Middleware
created_at: Wed Aug 1 14:02:00 -0500 2012
kind: article
categories: programming
draft: true
---

Like kombucha and man buns, middleware have become all the rage, and
just as I've learned to turn down my nose at any beverage that isn't
fermented and smelly, I've decided to incorporate middleware in every
facet of my code, including something as pedestrian as formatting
strings.

Yes, the library
[statsd-client](https://github.com/adzerk-oss/statsd-client) uses the
middleware pattern to create an extensible string formatter. As a
formatter, it takes arguments and produces a string:

```clojure
(require '[adzerk.statsd-client :as c])

(c/base-formatter (c/increment :googly.moogly 1))
; => "googly.moogly:1|c"
```

It's extensible in that you can create a new formatter by wrapping an
existing one, middleware style. In this next snippet, `dd/tags` is a
middleware, and it returns a new formatter that will take the argument
given by the `:tags` keyword and :

```clojure
(require '[adzerk.statsd-client.datadog :as dd])
(def tag-formatter (dd/tags c/base-formatter))

(tag-formatter (c/increment :googly.moogly 1 :tags ["yarb"]))
; => "googly.moogly:1|c|#yarb"
```

Aside from not wanting to be left out of the middleware craze, why
bother? After all, it's dead easy to create a function that takes
whatever arguments and produces a string.

Well here at Adzerk, we use [Datadog](https://www.datadoghq.com/) to
collect stats on a zerkillion servers (yeah, we use our name as a unit
of measurement (well no not really, it's just me)). Datadog gathers
your metrics using
[DogStatsd](http://docs.datadoghq.com/guides/dogstatsd/), which is
modeled after [statsd](https://github.com/etsy/statsd).

In case you're not familiar with
[statsd](https://github.com/etsy/statsd), it's "a network daemon that
runs on the Node.js platform and listens for statistics, like counters
and timers, sent over UDP or TCP and sends aggregates to one or more
pluggable backend services (e.g., Graphite)." Here at Adzerk, 
