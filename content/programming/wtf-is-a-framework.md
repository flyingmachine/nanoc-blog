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
some ways that Clojure is uniquely suited to creating a kick-ass
framework.

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
airtight, axiomatic, comprehensive description that programmers like.
One shortcoming is that the boundary between resource and application
is pretty thin: Postgres is an application in its own right, but from
the perspective of a Rails app it's a resoruce. Stil, hopefully my use
of _resource_ is clear enough that you nevertheless understand what tf
I'm talking about when I talk about resources.)

Coordinating these resources is inherently complex. You have to decide
how to create, validate, secure, and dispose of resources, and how to
convey one resource's entities to another resource, and how to deal
with issues like timing (race conditions) and failure handling that
arise whenever resources interact. Rails, for instance, was designed
to coordinate browsers, HTTP servers, and databases. It had to convey
user input to a database, and also retrieve and render database
records for display by the user interface, via HTTP requests and
responses. HTTP requests would get dispatched to a Controller, which
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
impossible because doing so requires experience. Beginners won't even
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
database operations like `save` and `destroy`. Additional messages the
abstraction responds to are `find`, `create`, `update`, and
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
can write to them using the same system calls as you'd use to write
files to disk - indeed, from your program's standpoint, all it knows
is that it's writing to a file; it doesn't know that the "file" might
actually be a pipe.

This is a huge part of UNIX's famed simplicity. It's what lets us run
this in a shell:

```
ls | wc
```

The shell interprets this by launching an `ls` process. Normally, when
the shell launches a process it sets three file descriptors (which,
remember, represent open files): `0` for `STDIN`, `1` for `STDOUT`,
and `2` for `STDERR`, and each of these file descriptors refer to your
terminal (terminals can be files!! what!?!?). Your shell sees the
pipe, `|`, and sets `ls`'s `STDOUT` to the pipe's `STDIN`, and the
pipe's `STDOUT` to `wc`'s `STDIN`. The pipe links processes' file
descriptors, while the processes get to read and write "files" without
having to know what's actually on the other end. (No joke, every time
I think of this I get a little excited tingle at the base of my
spine.)

This is why file I/O is referred to as _the universal I/O model_. I'll
have more to say about this in the next section, but I share it here
to illustrate how much more powerful your programming environment can
be if you find the right abstractions. The file I/O model still
dominates decades after its introduction, making our lives easier
_without our even having to understand how it actually works_.

One final point about abstractions: they provide mechanisms for
calling your application's code. We saw this a bit earlier with
ActiveRecord's lifecycle methods. Frameworks will usually provide the
overall structure for how an application should interact with its
environment, defining sets of events that you write custom handlers
for. With ActiveRecord lifecycles, the structure of `before_create`,
`create`, `after_create` is predetermined, but you can define what
happens at each step. This pattern is called _inversion of control_,
and many developers consider it a key feature of frameworks.

With *nix operating systems, you could say that in C programs the
`main` function is a kind of `onStart` callback. The OS calls `main`,
and `main` tells the OS what instructions should be run. However, the
OS controls when instructions are actually executed because the OS is
in charge of scheduling. It's a kind of inversion of control, right? 🤔

### Communication

Frameworks coordinate resources, and (it's almost a tautology to say
this) coordination requires _communication_. Communication is
_hard_. Frameworks make it easier by translating the disparate
"languages" spoken by different resources into one or more common
languages that are easy to understand and efficient, while also
ensuring extensibility and composability. Frameworks also do some of
the work of ensuring resilience. This usually entails:

* Establishing naming and addressing conventions
* Establishing conventions for how to structure content
* Introducing communication brokers
* Handling communication failures: the database is down! that file
  doesn't exist!

One example many people are familiar with is the HTTP stack, a
"language" that be used to communicate between browser and server
resources:

* HTTP structures content (request headers, request body)
* TCP handles communication failures
* IP handles addressing

#### Conventions

The file model is also a common language, and the OS uses device
drivers between it and whatever local language is spoken by hardware
devices. It has naming and addressing conventions, allowing you
specify files on the filesystem using character strings separated by
slashes that it translates to an internal inode (a data structure that
stores file and directory details, like ownership and
permissions). We're so used to this that it's easy to forget it's a
convention; *nix systems could have been designed so that you had to
refer to files using a number or a UUID. The file descriptors I
described in the last section are also a convention.

Another convention the file model introduces is to structure content
as byte streams, as opposed to bit streams, character streams, or xml
documents. However, bytes are usually too low-level, so the OS
includes a suite of command line tools that introduce the further
convention of structuring bytes by interpreting them as characters
(`sed`, `awk`, `grep`, and friends). More recently, more tools have
been introduced that interpret text as YAML or JSON. The Clojure world
has further tools to interpret JSON as transit. My YAML tools can't do
jack with your JSON files, but because these formats are all expressed
in terms of lower-level formats, the lower-level tools can still work
with them. Structure affects composability.

The file model's simplicity is what allows it to be the "universal I/O
model." I mean, just imagine if all Linux processes had to communicate
with XML instead of byte streams! Having a simple, universal
communication system makes it extremely easy for new resources to
participate without having to be directly aware of each other. It
allows us to easily compose command line tools. It allows one program
to write to a log while another reads from it. In other words, it
enables loose coupling and all the attendant benefits.

#### Communication Brokers

In particular, _globally addressable communication brokers_ (like the
filesystem, or Kafka queues, or databases) are essential to enabling
composable systems. _Global_ means that every resource has access to
it. _Addressable_ means that it's possible for any participant to
specify entities. _Communication broker_ means that the system's
purpose is convey data from one resource to another, and it has
well-defined semantics: a queue has FIFO semantics, the file system
has update-in-place semantics, etc.

If Linux had no filesystem and processes were only allowed to
communicate via pipes, it would be a nightmare. Indirect communication
is more flexible than direct communication. It supports decoupling
over time, in that reads and writes don't have to happen
synchronously. It also allows participants to drop in and out of the
communication system without a central controller. (By the way, I
can't think of the name for this concept or some better way to express
it, and would love feedback here.)

I think this is the trickiest part of framework design. At the
beginning of the article I mentioned that developers might end up
hacking around a framework's constraints, and I think the main
constraint is often the absence of a communication broker. The
framework's designers introduce new resources and abstractions, but
the only way to compose them is through direct communication, and
sometimes that direct communication is handled magically. If someone
wants to introduce new abstractions, they have to untangle all the
magic and hook deep into the framework's internals, using -- or even
overwriting! -- code that's meant to be private.

I remember running into this with Rails back when MongoDB was
released; the _document database_ resource was sufficiently different
from the _relational database resource_ that it was pretty much
impossible for MongoDB to take part in the ActiveRecord abstraction,
and it was also very difficult to introduce a new abstraction that
would play well with the rest of the Rails ecosystem.

For a more current example, a frontend framework might identify the
form as a resource, and create a nice abstraction for it that handles
things like validation and the submission lifecycle. If the form
abstraction is written in a framework that has no communication broker
(like a global state atom), then it will be very difficult to meet the
common use case of using a form to filter rows in a table because
there's no way for the code that renders table data to access the
form's state. You might come up with some hack like defining handlers
for exporting the form's state, but doing this on an ad-hoc basis
results in confusing and brittle code.

By contrast, the presence of a communication broker can make life much
easier. In the Clojure world, the React frameworks
[re-frame](https://github.com/Day8/re-frame/) and
[om.next](https://github.com/omcljs/om) have embraced global state
atoms, a kind of communication broker similar to the filesystem (atoms
are an in-memory storage mechanism). They also both have well defined
communication protocols. I'm not very familiar with
[Redux](https://redux.js.org/) but I've heard tell that it also has
embraced a global, central state container.

If you create a form abstraction using re-frame, it's possible to keep
track of its state in a global state atom. It's further possible to
establish a naming convention for forms, making it easier for other
participants to look up the form's data and react to it.

Communication systems are fundamental. Without them, it's difficult to
build anything but the simplest applications. By providing
communication systems, frameworks relieve much of the cognitive burden
of building a program. By establishing communication standards,
frameworks make it possible for developers to create composable tools,
tools that benefit everybody who uses that framework. Standards make
infrastructure possible, and infrastructure enables productivity.

In this section I focused primarily on the file model because it's
been so successful and I think we can learn a lot from it. Other
models include event buses and message queues.

### Environments

Frameworks are built to coordinate resources within a particular
_environment_. When we talk about desktop apps, web apps, single page
web apps, and mobile apps, we're talking about different
environments. From the developer's perspective, environments are
distinguished by the resources that are available, while from the
user's perspective different environments entail different usage
patterns and expectations about availability, licensing, and payment.

As technology advances, new resources become available (the Internet!
databases! smart phones! powerful browsers! AWS!) and new environments
evolve to combine those resources, and frameworks are created to
target those environments. This is why we talk about mobile frameworks
and desktop frameworks and the like.

## More Benefits of Using Frameworks

So far I've mostly discussed how frameworks bring benefits to the
individual developer. In this section I'll explain how frameworks
benefit communities, how they make programming fun, and (perhaps most
importantly) how they are a great boon for beginners.

First, to recap, a framework is a set of libraries that:

* Manages the complexity of coordinating the _resources_ needed to
  write an application
* By providing _abstractions_ for those resources
* And _systems for communicating_ between those resources
* Within an _environment_
* So that programmers can focus on writing business logic

This alone lifts a huge burden off of developers. In case I haven't
said it enough, this kind of work is _hard_, and if you had to do it
every time you wanted to make an application it would be frustrating
an exhausting. Actually, let me rephrase that: I _have_ had to do this
work, and it's frustrating and exhausting. It's why Rails was such a
godsend when I first encountered it in 2005.

### Frameworks Bring Community Benefits

Clear abstractions and communication systems allow people to share
modules, plugins, or whatever you want to call framework extensions,
creating a vibrant ecosystem of reusable components.

If you accept my assertion that an Operating System is a framework,
then you can consider any program which communicates via one of the
OS's communication systems (sockets, the file model, etc) to be an
extension of the framework. Postgres is a framework extension that adds
an RDBMS resource. statsd is an extension that adds a monitoring
resource.

Similarly, Rails makes it possible for developers to identify
specialized resources and extend the framework to easily support
them. One of the most popular and powerful is
[Devise](https://github.com/plataformatec/devise), coordinates Rails
resources to introduce a new authentication resource. Just as using
Postgres is usually preferable to rolling your own database, using
Devise is usually preferable to rolling your own authentication
system.

Would it be possible to create a Devise for Clojure? I don't think
so. Devise is designed to be database agnostic, but because Clojure
doesn't really have a go-to framework that anoints or introduces a
database abstraction, no one can write the equivalent of Devise in
such a way that it could easily target any RDBMS. Without a framework,
it's unlikely that someone will be able to write a full-featured
authentication solution that you can reuse, and if you write one it's
unlikely others would see much benefit if you shared it. I think it's
too bad that Clojure is missing out on these kinds of ecosystem
benefits.

### Fun

If you still think frameworks are overkill or more trouble than
they're worth, believe me I get it. When I switched from Rails to
Clojure and its "libraries not frameworks" approach, I _loved_ it. A
framework felt unnecessary because all the pieces were so simple that
it was trivial for me to glue them together myself. Also, it was just
plain fun to solve a problem I was familiar with because it helped me
learn the language.

Well, call me a jaded millenial fart, but I don't think that this work
is fun anymore. I want to build products, not build the infrastructure
for building products. I want a plugin that will handle the reset
password process for me. I want an admin panel that I can get working
in five minutes.

For me, programming is a creative endeavor. I love making dumb things
and putting them in front of people to see what will happen. Rails let
me build (now defunct) sites like phobiatopia.com, where users could
share what they're afraid of and the site would use their IP address
to come up with some geo coordinates and use Google Maps to display a
global fear map. A lot of people were afraid of bears.

### Beginners

There's a kind of scorn for beginners

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

* simple made easy
* simplicity matters
