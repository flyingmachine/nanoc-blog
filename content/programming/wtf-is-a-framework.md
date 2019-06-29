---
title: Frameworks and Why (Clojure) Programmers Need Them
created_at: Sat Jul 20 2013 19:23:00 -0500
kind: article
categories: programming
summary: "In the Clojure community, one of the unwritten tenets is that frameworks can eat shit."
draft: true
---

In the Clojure community, one of the unwritten tenets is that
frameworks can eat shit. Other languages might need frameworks, but
not ours! Libraries all the way, baby!

This attitude did not develop without reason. Many of us came to
Clojure after getting burned on magical monolith web app frameworks
like Rails, where we ended up spending an inordinate amount of time
coming up with hacks for the framework's shortcomings. Another
"problem" is that Clojure tools like
[Luminus](http://www.luminusweb.net/) and the top-rate web dev
libraries it bundles provide such a productive experience that
frameworks seem superfluous.

Be that as it may, I'm going to make the case for why the community's
dominant view of frameworks -- specifically, web frameworks, since
that what I'm most familiar with -- needs revision. To explain why,
I'll first define _framework_ in a way that I hope you'll find both
novel and satisfying. Then I'll explain the benefits frameworks bring
to you as an individual developer and to the broader Clojure
ecosystem. Lastly, we'll engage in some techno-futurism and dream up
some ways that Clojure is uniquely suited to creating a completely
kick-ass framework.

## What is a Framework?

A framework is a set of libraries that:

* Manages the complexity of coordinating the _resources_ needed to
  write an application
* By providing _abstractions_ for those resources
* And _systems for communicating_ between those resources
* Within an _environment_
* So that programmers can focus on writing business logic

I'll elaborate on each of these points by exploring them in relation
to Rails and to the ultimate framework: the operating system. Briefly:
an operating system provides virtual abstractions for hardware
resources so that programmers don't have to focus on the details of,
say, pushing bytes onto some particular disk or managing CPU
scheduling. It also provides the conventions of a hierarchical
filesystem with an addressing system consisting of names separated by
forward slashes, and these conventions provide one way for resources
to communicate with each other (Process A can write to `/foo/bar`
while Process B reads from it) - if every programmer came up with her
own bespoke addressing system, it would be a disaster. The OS handles
this for us so we can focus on application-specific tasks.

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
concurrent programming much easier than anything provided by the OS or
by the JVM's concurrency libraries. It's a virtual computation model
that's then compiled to JVM bytecode (or JavaScript!), which then has
to be executed by operating system processes.

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
convey one resource's entities to another resource, and how to deal
with issues like timing, race conditions, and failure handling that
arise whenever resources interact. Rails, for instance, was designed
to coordinate browsers, HTTP servers, and databases. It had to convey
user input to a database, and also retrieve and render database
records for display by the user interface, via HTTP requests and
responses.  HTTP requests would get dispatched to a Controller, which
was responsible for interacting with a database and making data
available to a View, which would render HTML that could be sent back
to the browser.

You don't _have_ to coordinate web app resources using the MVC
approach Rails uses, but you do have to coordinate these resources
_somehow_. These decisions involve making tradeoffs and imposing
constraints to achieve a balance of extensibility (creating a system
generic enough for new resources to participate) and power (allowing
the system to fully exploit the unique features of a specific
resource.)

This is a very difficult task even for experienced developers, and the
choices you make could have negative repercussions that aren't
apparent until you're heavily invested in them. With Rails, for
instance, ActiveRecord provided a good generic abstraction for
databases, but early on it was very easy to produce extremely
inefficient SQL, and sometimes very difficult to produce efficient
SQL. You'd often have to hand-write SQL, eliminating some of the
benefits of using AR in the first place.

For complete beginners, the task of making these tradeoffs is
impossible because to do so requires experience. Beginners won't even
know that it's necessary to make these decisions.

Frameworks make these decisions for us, allowing us to focus on
business logic, and they do so by introducing _communication systems_
and _abstractions_.

### Resource Abstractions

Our software interacts with resources via their _abstractions_. I
think of abstractions as:

* the data structures used to represent a resource
* the set of messages that a resource responds to
* the mechanisms the resource uses to call your application's code

(_Abstraction_ might be a terrible word to use here. Every developer
over three years old has their own definition, and if mine doesn't
correspond to yours just cut me a little slack and run with it :)

Rails exposes a database resource that your application code interacts
with via the `ActiveRecord` abstraction. Tables correspond to classes,
and rows to objects of that class. This a choice with tradeoffs - rows
could have been represented as Ruby hashes, which might have made them
more portable while making it more difficult to concisely express
database operations like `save` and `destroy`.  Some of the messages
the abstraction responds to are `find`, `create`, `update`, and
`destroy`. It calls your application's code via lifecycle callback
methods like `before_validation`. Often, these callbacks are
_synthetic_, meaning they are introduced by the framework and not
provided by the resource.

As another example, *nix operating systems introduce the _file_
abstraction, whose core functions are `open`, `read`, `write`, and
`close`. Files are represented as sequential streams of bytes, which
is just as much a choice as ActiveRecord's choice to use Ruby
objects. Open files are represented as _file descriptors_, which are
usually a small integer. As [_The Linux Programming
Interface_](https://amzn.to/2FK39zQ) (one of the best programming
books ever written) describes:

> The following are the four key system calls for performing file I/O:
>
> * _fd = open(pathname, flags, mode)_ opens the file identified by
>   _pathname_, returning a file descriptor used to refer to the open
>   file in subsuquent calls.
> * _numread_ = read(fd, buffer, count)_ reads at most _count_ bytes
>   from the open file referred to by _fd_ and stores them in _buffer_.
> * _numwriten = write(fd, buffer, count)_ writes up to _count_ bytes
>   from _buffer_ to the open file referred to by _fd_.
> * _status = close(fd)_ is  called after all I/O has been completed,
>   in order to release the file descriptor _fd_ and its associated
>   kernel resources.

Now here's the amazing magical kicker: _file_ doesn't have to mean
_file on disk_. The OS implements the file interface for **pipes**,
terminals, sockets, and other resources, meaning that your programs
can write to them using the same system calls as you'd use to
write files to disk - indeed, from the program's standpoint, all it
knows is that it's writing to a file; it doesn't know that the "file"
might actually be a pipe. 

This is a huge part of UNIX's famed simplicity. It's what lets us run
this in a shell:

```
ls | grep *.log
```

The shell interprets this by launching an `ls` process. Normally, when
the shell launches a process it sets three file descriptors (which,
remember, represent open files): `0` for `STDIN`, `1` for `STDOUT`,
and `2` for `STDERR`, and each of these file descriptors refer to your
terminal (terminals can be files!! what!?!?). Your shell sees the
pipe, `|`, and sets `ls`'s `STDOUT` to the pipe's `STDIN`, and the
pipe's `STDOUT` to `greps`'s `STDIN`. The pipe links processes' file
descriptors, while the processes get to read and write "files" without
having to know what's actually on the other end. (No joke, every time
I think of this I get a little excited tingle at the base of my
spine.)

This is why file I/O is referred to as _the universal I/O model_. I'll
have more to say about this in the next section, but I share it here
to illustrate how much more powerful your programming environment can
be if you find the right abstractions. The file I/O model still
dominates decades after its introduction, making our lives easier
_without even having to understand how it actually works_.

One final point about abstractions: they provide mechanisms for
calling your application's code. We saw this a bit earlier with
ActiveRecord's lifecycle methods. Frameworks will usually provide the
overall structure for how an application should interact with its
environment, defining sets of events for you to write custom handlers
for. With ActiveRecord lifecycles, the structure of `before_create`,
`create`, `after_create` is predetermined, but you can define what
happens at each step. This pattern is called _inversion of control_,
and many developers consider it a key feature of frameworks.

With *nix operating systems, you could say that programs written in C
implement a kind of `onStart` callback using a function named
`main`. The OS calls `main`, and `main` tells the OS what instructions
should be run. However, the OS controls when instructions are actually
executed because the OS is in charge of scheduling. It's a kind of
inversion of control. Think about it.

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


There is almost scorn for the beginner. Leaders in the community
explicitly position Clojure as a language for experienced developers.

In one of Rich's talks he discusses mastery in relation to music, and
how we don't try to dumb down the guitar to make it easier. No, but we
can come up with better ways to introduce people to guitar. When I
first tried to learn guitar, I tried to learn how to read music, play
complex songs, and sing. I just started again this year, and I've
found better resources. The guide I'm using has me learning just three
chords and how to switch between them. Next step is singing a song
with them. Whereas before I never got anywhere and gave up, now I'm
actually making music. I have the joy of singing an actual song. We
can do that with apps - make it easy for someone to sing a simple
song, and provide the resources they need for mastery.
