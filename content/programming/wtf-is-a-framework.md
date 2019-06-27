---
title: (Web) Frameworks and Why (Clojure) Programmers Need Them
created_at: Sat Jul 20 2013 19:23:00 -0500
kind: article
categories: programming
summary: "In the Clojure community one of the unwritten tenets is that frameworks can eat shit."
draft: true
---

In the Clojure community, one of the unwritten tenets is that
web frameworks can eat shit. Other languages might need frameworks, but
not ours! Libraries all the way, baby!

This attitude did not develop without reason. Many of us came to
Clojure after getting burned on magical monolith app frameworks like
Rails, where we ended up spending an inordinate amount of time coming
up with hacks for the framework's shortcomings. Another "problem" is
that Clojure tools like [Luminus](http://www.luminusweb.net/) and the
top-rate web dev libraries it bundles provide such a productive
experience that frameworks seem superfluous.

Be that as it may, I'm going to make the case for why the Clojurians'
view of frameworks needs revision. To explain why, I'll first share a
definition of what a framework actually is that I hope you'll find
both novel and satisfying. Then I'll explain the benefits frameworks
bring. Lastly, we'll engage in some techno-futurism and dream up some
ways that Clojure can be used to create a completely kick-ass
framework.

## What is a Framework?

A framework is a library that provides:

* _Abstractions_ for _resources_
* And systems for _communicating_ between those resources
* Within an _environment_
* So that programmers can focus on writing code that uses those
  resources to achieve some _business goal_

I'll elaborate on each of these points by exploring them in relation
to the ultimate framework: the operating system. Briefly: an operating
system provides virtual abstractions for hardware resources so that
programmers don't have to focus on the details of, say, pushing bytes
onto some particular disk or managing a CPU's cache. It also provides
the conventions of a hierarchical file-system with an addressing
system consisting of names separated by forward slashes, and these
conventions provide one way for resources to communicate with each
other (Process A can write to `/foo/bar` while Process B reads from
it) - if every programmer came up with her own bespoke addressing
system, it would be a disaster. The OS handles this for us so we can
focus on application-specific tasks.

### Abstractions for Resources

_Resources_ are the "materials" used by programs to do their work, and
can be divided into four categories: storage, computation, networks,
and interfaces. Operating systems were developed to manage bare
hardware resources. It's something we take for granted now, but back
in ye olden times programmers had to write every single instruction
for every aspect of running a program, including writing results to
clay tablets or whatever they used back then.

We start with hardware and build virtual resources on top. For
example, with storage the OS starts with disks and memory and creates
the virtual filesystem as a storage resource on top. Databases like
Postgres use the filesystem to create another virtual storage
resource to handle use cases not met by the filesystem. Recently
we've even seen databases like Datomic use other databases like
Cassandra or DynamoDB as their storage layer. Browsers create their
own virtual environments and introduce new resources like local
storage and cookies.

For computation, the OS introduces processes and threads as virtual
resources representing and organizing program execution. I'm not
familiar with Erlang but my understanding is its BEAM virtual machine
creates an environment with a process model that's dramatically
different from the underlying operating system's. Clojure's
`core.async` library also provides a computational model
(communicating sequential processes) that's meant to make async and
concurrent programming much easier to code for and reason about than
anything provided by the OS or by the JVM's concurrency
libraries. It's a virtual computation model that's then compiled to
JVM bytecode (or JavaScript!), which then has to be executed by
operating system processes.

Interfaces follow the same pattern: on the visual display side, the OS
paints to monitors, applications paint to their own virtual canvas,
browsers are applications which introduce their own resources (the DOM
and `<canvas>`), and React introduces a virtual DOM.

Resources can manage their own _entities_: in a database, entities
could include tables, rows, triggers, and sequences. Filesystem
entities include directories and files. A GUI manages windows, menu
bars, and other components.

I realize that this description of _resource_ is not the kind of
airtight, axiomatic, comprehensive description that programmers like,
but hopefully it's good enough that you nevertheless understand what
I'm talking about when I'm talking about resources.

We write software to orchestrate the interaction of a constellation of
resources to achieve some business end. Our software interacts with
resources via their _abstractions_. I think of abstractions as:

* the set of messages that a resource responds to
* the mechanisms the resource uses to call your application's code

Rails provides a database resource that your application code
interacts with via `ActiveRecord`. Some of the messages it responds to
are `find`, `create`, `update`, and `destroy`. It calls your
application's code via lifecycle callback methods like
`before_validation`.

A framework will often call your code via lifecycle callbacks. React's
component resource introduces `componentWillUpdate`,
`componentWillUnmount` and the like. A framework might also implement
global events that your code can listen for; operating systems have
_signals_ and if I'm not mistaken Apple's Cocoa framework had some
kind of global event bus.

Thanks to this separation of concerns, framework developers can focus
on improving the performance, reliability, and other quality
attributes of the resources they provide abstractions for. The
filesystem ab

Resources are extensible.

The filesystem abstraction hides implementation details about.

Part of what makes a framework a framework is that it allows the
introduction of new resources that the framework's authors didn't
anticipate. For that to be possible, the framework must have a robust
communication system.

### Communication

### Environments

### Business Goal

## What isn't a framework?

A framework is a library whose purpose is to 

Frameworks are like operating systems: their job is to provide
abstractions for resources and conventions for how the resources
coordinate with each other.

In fact, an operating system _is_ a framework.

And how do frameworks differ from libraries?

Allow you to write application-specific code, minimize the need to
write resource-specific code. They provide conventions for how to
represent and interact with resources. They're about coordinating
various resources (lifecycles). Handling events, making it possible
for the resulting state to be conveyed across resources.

They allow you to convey events across
resources, and they provide resource lifecycles.


- application frameworks vs computation like fork/join - fork/join is
  a library
- on one level, libraries are part of the artifact ecosystem,
  frameworks are distributed as libraries
- distinguished from libraries by their purpose - enter the domain of
  architecture
- libraries are focused on solving a narrower problem: performing
  certain kinds of computations, wrapping certains kinds of resources
- examples of frameworks
  - cocoa
  - operating systems
  - rails

## What Makes a Good Framework?

## Why We Need Frameworks

- a platform for ongoing resource integration
- a tool for newcomers

## A Clojure Framework

