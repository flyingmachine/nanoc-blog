---
title: Frameworks and Why (Clojure) Programmers Need Them
created_at: Sat Jul 20 2013 19:23:00 -0500
kind: article
categories: programming
summary: "In the Clojure community, one of the unwritten tenets is that frameworks can eat shit."
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

Be that as it may, I'm going to make the case for why the community's
dominant view of frameworks needs revision. To explain why, I'll first
share a definition of what a framework actually is that I hope you'll
find both novel and satisfying. Then I'll explain the benefits
frameworks bring to you as an individual developer and to the broader
Clojure ecosystem. Lastly, we'll engage in some techno-futurism and
dream up some ways that Clojure is uniquely suited to creating a
completely kick-ass framework.

## What is a Framework?

A framework is a set of libraries that:

* Manages the complexity of coordinating the _resources_ needed to
  write an application
* By providing _abstractions_ for those resources
* And _systems for communicating_ between those resources
* Within an _environment_
* So that programmers can focus on writing business logic

I'll elaborate on each of these points by exploring them in relation
to the ultimate framework: the operating system. Briefly: an operating
system provides virtual abstractions for hardware resources so that
programmers don't have to focus on the details of, say, pushing bytes
onto some particular disk or managing CPU scheduling. It also provides
the conventions of a hierarchical file-system with an addressing
system consisting of names separated by forward slashes, and these
conventions provide one way for resources to communicate with each
other (Process A can write to `/foo/bar` while Process B reads from
it) - if every programmer came up with her own bespoke addressing
system, it would be a disaster. The OS handles this for us so we can
focus on application-specific tasks.

### Coordinating Resources

_Resources_ are the "materials" used by programs to do their work, and
can be divided into four categories: storage, computation, networks,
and interfaces. Examples of storage include files, databases, caches,
search engines, and more. Computation examples include processes,
threads, actors, background jobs, and parallel jobs. Network examples
include HTTP requests and message queues. Interfaces typically include
screens and the systems used to display stuff on them: gui toolkits,
browsers and the DOM, etc.

Specialized resources are built on top of more general-purpose
resources. We start with hardware and build virtual resources on
top. For example, with storage the OS starts with disks and memory and
creates the filesystem as a (virtual) storage resource on
top. Databases like Postgres use the filesystem to create another
virtual storage resource to handle use cases not met by the
filesystem. Recently we've even seen databases like Datomic use other
databases like Cassandra or DynamoDB as their storage layer. Browsers
create their own virtual environments and introduce new resources like
local storage and cookies.

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

Resources manage their own _entities_: in a database, entities could
include tables, rows, triggers, and sequences. Filesystem entities
include directories and files. A GUI manages windows, menu bars, and
other components.

(I realize that this description of _resource_ is not the kind of
airtight, axiomatic, comprehensive description that programmers like,
but hopefully it's good enough that you nevertheless understand what
tf I'm talking about when I talk about resources.)

Coordinating these resources is inherently complex. You have to decide
how to create, validate, secure, and dispose of resources, and how to
convey one resource's entities to another resource. Rails, for
instance, was designed to coordinate browsers, HTTP servers, and
databases. It had to convey user input to a database, and also
retrieve and render database records for display by the user
interface, via HTTP requests and responses.  HTTP requests would get
dispatched to a Controller, which was responsible for interacting with
a database and making data available to a View, which would render
HTML that could be sent back to the browser.

You don't _have_ to coordinate web app resources using the MVC
approach Rails uses, but you do have to coordinate these resources
_somehow_. These decisions involve making tradeoffs and imposing
constraints. This is a very difficult task even for experienced
developers because the choices you make could have negative
repercussions that aren't apparent until you're heavily invested in
them. For complete beginners, the task is impossible because making
these decisions requires experience. Beginners won't even understand
that it's necessary to make these decisions.

Frameworks make these decisions for us, allowing us to focus on
business logic, and they do so by introducing _communication systems_
and _abstractions_.

### Resource Abstractions

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

It's worth noting that frameworks can add their own functionality
around an existing reosurce. ActiveRecord's callbacks are layered on
top of database interactions.

*the need for new resources*

- forms
- ajax calls
- pagination
- routing
- workers

Resources are extensible.

The filesystem abstraction hides implementation details about.

Part of what makes a framework a framework is that it allows the
introduction of new resources that the framework's authors didn't
anticipate. For that to be possible, the framework must have a robust
communication system.

### Communication

- the filesystem
- IPC
- signals
- sockets

in the browser

- cookies
- ajax calls

### Environments

- inversion of control: receiving events from the environment

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
- development of resources independently of the code that uses them;
  separation of concerns
- common skillset for developers

## A Clojure Framework

Just because you can't be everything to everybody doesn't mean you
can't be something to most people.

Immutability provides a remarkable foundation.

integrant receives the external message: start it also manages
composition, or how pieces interact with each other it makes it easy
to break down a component into subcomponents, allowing different
strategies and avoiding lock-in

philosophy of simplicity and decomplecting

data composition - better than hiding data
in OO systems it's necessary to hide data

like in a filesystem, you can coordinate different components
indirectly, gaining independence of each other - loose coupling

- do they communicate using an open, universal standard? or by direct
  access?
- example of tight coupling at the OS level might be reading and
  writing to memory addresses
