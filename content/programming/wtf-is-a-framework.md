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
Clojure after getting burned on magical frameworks like Rails, where
we ended up spending an inordinate amount of time coming up with hacks
for the framework's shortcomings. Another "problem" is that Clojure
tools like [Luminus](http://www.luminusweb.net/) and the top-rate web
dev libraries it bundles provide such a productive experience that
frameworks seem superfluous.

Be that as it may, I'm going to make the case for why the community's
dominant view of frameworks needs revision. Frameworks are
useful. They should not all universally eat shit. To convince you,
I'll start by explaining what a framework is. I have yet to read a
definition of _framework_ that satisfies me, and I think a lot of the
hate directed at them stems from a lack of clarity about what exactly
they are. Are they just libraries? Do they have to be magical?  Is
there some law decreeing that they have to be more trouble than
they're worth? All this and more shall be revealed.

I think the utility of frameworks will become evident by describing
the purpose they serve and the ways they serve that purpose. I also
think that the description will clarify what makes a _good_ framework,
and provide some diagnostic criteria for why some frameworks end up
hurting us. My hope is that you'll find this discussion interesting
and satisfying, and that it will give you a new, useful perspective
not just on frameworks but on programming in general.

Frameworks have second-order benefits, and I'll cover those too. They
make it possible for an ecosystem of reusable components to
exist. They make programming fun. They make it easier for beginners to
make stuff.

Last, I'll cover some ways that I think Clojure is uniquely suited to
creating kick-ass frameworks.

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
can be divided into four categories: storage, computation,
communication, and interfaces. Examples of storage include files,
databases, caches, search engines, and more. Computation examples
include processes, threads, actors, background jobs, and parallel
jobs. Communication examples include HTTP requests and message
queues. Interfaces typically include screens and the systems used to
display stuff on them: gui toolkits, browsers and the DOM, etc.

Specialized resources are built on top of more general-purpose
resources. (You might refer to these specialized resources _services_
or _components_.) We start with hardware and build virtual resources
on top. For example, with storage the OS starts with disks and memory
and creates the filesystem as a virtual storage resource on
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
the perspective of a Rails app it's a resoruce. Still, hopefully my
use of _resource_ is clear enough that you nevertheless understand
what tf I'm talking about when I talk about resources.)

Coordinating these resources is inherently complex. You have to decide
how to create, validate, secure, and dispose of resources, and how to
convey one resource's entities to another resource, and how to deal
with issues like timing (race conditions) and failure handling that
arise whenever resources interact. Rails, for instance, was designed
to coordinate browsers, HTTP servers, and databases. It had to convey
user input to a database, and also retrieve and render database
records for display by the user interface, via HTTP requests and
responses.

There is no obvious or objectively correct way to coordinate these
resources. In Rails, HTTP requests would get dispatched to a
Controller, which was responsible for interacting with a database and
making data available to a View, which would render HTML that could be
sent back to the browser.

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
"language" used to communicate between browser and server resources:

* HTTP structures content (request headers and request body as text)
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
composable systems. _Global_ means that every resource can have access
to it. _Addressable_ means that it's possible for any participant to
specify entities. _Communication broker_ means that the system's
purpose is to convey data from one resource to another, and it has
well-defined semantics: a queue has FIFO semantics, the file system
has update-in-place semantics, etc.

If Linux had no filesystem and processes were only allowed to
communicate via pipes, it would be a nightmare. Indirect communication
is more flexible than direct communication. It supports decoupling
over time, in that reads and writes don't have to happen
synchronously. It also allows participants to drop in and out of the
communication system independently of each other. (By the way, I can't
think of the name for this concept or some better way to express it,
and would love feedback here.)

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

One of the reasons I stopped using Rails was because it was a _web
application framework_, but I wanted to build _single page
applications_. At the time (around 2012?), I was learning to use
Angular and wanted to deploy applications that used it, but it didn't
really fit with Rails's design.

And that's OK. Some people write programs for Linux, some people write
for macOS, some people still write for Windows for some reason (just
kidding! don't kill me!). A framework is a tool, and tools are built
for a specific purpose. If you're trying to achieve a purpose the tool
isn't built for, use a different tool.

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
work, and it _is_ frustrating and exhausting. It's why Rails was such
a godsend when I first encountered it in 2005.

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
go-to database abstraction, no one can write the equivalent of Devise
in such a way that it could easily target any RDBMS. Without a
framework, it's unlikely that someone will be able to write a
full-featured authentication solution that you can reuse, and if you
write one it's unlikely others would see much benefit if you shared
it. I think it's too bad that Clojure is missing out on these kinds of
ecosystem benefits.

Another subtler benefit frameworks bring is that they present a
coherent story for how developers can build applications in your
language, and that makes your language more attractive. Building an
application means coordinating resources for the environment you're
targeting (desktop, mobile, SPA, whatever). If your language has no
frameworks for a target environment, then learning or using the
language is much riskier. There's a much higher barrier to building
applications: not only does a dev have to learn the language's syntax
and paradigms, she has to figure out how to perform the complex task
of abstracting and coordinating resources using the language's
paradigms.

### Frameworks Make Development Fun

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
in five minutes. Frameworks handle the kind of work that ideally only
has to be done once. I don't want to have to do this work over and
over every time I want to make something.

For me, programming is a creative endeavor. I love making dumb things
and putting them in front of people to see what will happen. Rails let
me build (now defunct) sites like phobiatopia.com, where users could
share what they're afraid of and the site would use their IP address
to come up with some geo coordinates and use Google Maps to display a
global fear map. A lot of people were afraid of bears.

Frameworks let you focus on the fun parts of building an app. They let
you release an idea, however dumb, more quickly.

### Frameworks Help Beginners

Frameworks help beginners by empowering them to build real,
honest-to-god running applications that they can show to their friends
and even make money with, without having to fully understand or even
be aware of all the technology they're using. Being able to conjure up
a complete creation, no matter how small or ill-made, is the very
breath of wonder and delight.

There's a kind of thinking that says frameworks are bad because they
allow beginners to make stuff without having to know how it all
works. ActiveRecord is corrupting the youth, allowing them to build
apps without even knowing how to pronounce _SQL_.

Hogwash. Fiddlefaddle. Poppycock. If that's how you feel, maybe you
should roll your own operating system. I will always and forever
disagree with people who argue against making it easier for beginners
to experience the joy of creation.

Unfortunately, some in the Clojure community subscribe to the idea
that it's misguided to make tools easier for beginners to use,
including Clojure's creator Rich Hickey. From his talk [Design,
Composition, and
Performance](https://www.infoq.com/presentations/Design-Composition-Performance/):

> Instruments are made for people who can play them... They're made
> for people who can actually play them. And that's a problem, right?
> Because beginners can't play. They're not yet players, they don't
> know how to do it.
>
> ...We should fix like, the cello. Should cellos auto-tune? Or maybe
> they should have red and green lights? It's green when it's in-tune
> and it's red when it's out of tune. Or maybe they shouldn't make any
> sound at all until you get it right. Is that how it works?
>
> No, that's not how it works! Look at these kids. (slide shows a
> picture of kids playing cello). They're being subjected to
> cellos. There's nothing helping them here. But otherwise -- they're
> smaller, but those are real cellos. They're hard to play, they're
> awkward, they sound terrible, they're going to be out of tune. It's
> going to be tough, for a while, for these kids.
>
> But if they had any of those kinds of aids, they would never
> actually learn how to play cello. They'd never learn to hear
> themselves, or to tune themselves, or to listen. And playing a cello
> is about being able to hear, more than anything else.
>
> ...We should not sell humanity short by trying to solve the problem
> of beginners in our stuff.
>
> ...Just as we shouldn't target beginners in our designs, nor should
> we try to eliminate all effort. It's an anti-pattern. It's not going
> to yield a good instrument. It's OK for there to be effort.
>
> ...Coltrane couldn't build a web site in a day. I don't know why
> this has become so important to us. It's really like a stupid thing
> to be important, especially to an entire industry

I don't understand this argument. I don't understand what prompted
it. It's bizarre and self-contradictory: on the one hand, cellos are
made for players and we shouldn't try to change them to accommodate
novices, but on the other hand Rich acknowledges child learners _play
child-sized cellos_. I'm quite sure that Yo-Yo Ma doesn't play a
child-sized cello. Maybe pilots should forego flight simulators and
only learn to fly with actual planes. The rant defies logic, but it's
there any way, which makes me conclude that its only purpose is to
heap scorn on the notion of accommodating beginners, and on the idea
that beginners might need accommodation.

What really gets me is this bit:

> Coltrane couldn't build a web site in a day. I don't know why this
> has become so important to us. It's really like a stupid thing to be
> important, especially to an entire industry.

Yes indeed, why does the industry care at all about making it easier
for novices to create products? It's so stupid!

The talk also raises and dismisses the idea of using red and green
lights to tell the player when he's in tune or out of town. This is
funny because years ago I started learning to play violin, and as I
was learning the finger positions I would keep a tuner on to give me
feedback on when I was in tune and out of tune. I didn't know what
in-tune and out-of-tune sounded like, or where to position my fingers
to create the correct sounds. Using the tuner is what actually helped
me learn how to listen.

One more counter-example: I am a photographer. My instrument, if you
want to call it that, is the camera. I have a professional camera, and
I know how to use it. The photos I've been creating require a fair
amount of technical knowledge and specialized equipment:

(insert photo here)

Yet somehow I'm able to enjoy myself and my craft without saying it's
stupid that point-and-shoot cameras exist and that companies cater to
budding photographers (or even people who only take casual snapshots),
and that these babies need to get callouses on their hands from
handling real cameras.

Novices benefit greatly from experts to guiding them. I don't think
you can become a master photographer using your phone's camera, but
you can take some damn good photos and be proud of them. And if you do
want to become a master, that kind of positive feedback and sense of
accomplishment will give you the motivation to stick with it and learn
the hard stuff.

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


* simple made easy
* simplicity matters
