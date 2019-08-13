---
title: Frameworks and Why (Clojure) Programmers Need Them
created_at: Tue Aug 13 2019 16:00:00 -0500
kind: article
categories: programming
summary: "In the Clojure community, one of the unwritten tenets is that frameworks can eat shit."
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
definition of _framework_ that satisfies me, and I think some of the
hate directed at them stems from a lack of clarity about what exactly
they are. Are they just glorified libraries? Do they have to be
magical?  Is there some law decreeing that they have to be more
trouble than they're worth? All this and more shall be revealed.

I think the utility of frameworks will become evident by describing
the purpose they serve and how they achieve that purpose. The
description will also clarify what makes a _good_ framework and
explain why some frameworks end up hurting us. My hope is that you'll
find this discussion interesting and satisfying, and that it will give
you a new, useful perspective not just on frameworks but on
programming in general. Even if you still don't want to use a
framework after you finish reading, I hope you'll have a better
understanding of the problems frameworks are meant to solve and that
this will help you design applications better.

Frameworks have second-order benefits, and I'll cover those too. They
make it possible for an ecosystem of reusable components to
exist. They make programming fun. They make it easier for beginners to
make stuff.

Last, I'll cover some ways that I think Clojure is uniquely suited to
creating kick-ass frameworks.

(By the way: I've written this post because I'm building a Clojure
framework! So yeah this is totally my Jedi mind trick to prime you to
use _my_ framework. The framework's not released yet, but I've used it
to build [Grateful Place, a community for people who are into
cultivating gratitude, compassion, generosity, and other positive
practices](https://gratefulplace.com). Just as learning Clojure makes
you a better programmer, learning to approach each day with
compassion, curiosity, kindness, and gratitude will make you a more
joyful person. If you want to brighten your day and mine, please
join!)

## What is a Framework?

A framework is a set of libraries that:

* Manages the complexity of coordinating the _resources_ needed to
  write an application...
* by providing _abstractions_ for those resources...
* and _systems for communicating_ between those resources...
* within an _environment_...
* so that programmers can _focus on writing the business logic_ that's
  specific to their product

I'll elaborate on each of these points using examples from
[Rails](https://rubyonrails.org/) and from the ultimate framework: the
_operating system_. 

You might wonder, how is an OS a framework? When you look at the list
of framework responsibilities, you'll notice that the OS handles all
of them, and it handles them exceedingly well. Briefly: an OS provides
virtual abstractions for hardware resources so that programmers don't
have to focus on the details of, say, pushing bytes onto some
particular disk or managing CPU scheduling. It also provides the
conventions of a hierarchical filesystem with an addressing system
consisting of names separated by forward slashes, and these
conventions provide one way for resources to communicate with each
other (Process A can write to `/foo/bar` while Process B reads from
it) - if every programmer came up with her own bespoke addressing
system, it would be a disaster. The OS handles this for us so we can
focus on application-specific tasks.

Because operating systems are such successful frameworks we'll look at
a few of their features in some detail so that we can get a better
understanding of what good framework design looks like.

### Coordinating Resources

_Resources_ are the "materials" used by programs to do their work, and
can be divided into four categories: storage, computation,
communication, and interfaces. Examples of storage include files,
databases, and caches. Computation examples include processes,
threads, actors, background jobs, and core.async processes. For
communication there are HTTP requests, message queues, and event
buses. Interfaces typically include keyboard and mouse, plus screens
and the systems used to display stuff on them: gui toolkits, browsers
and the DOM, etc.

Specialized resources are built on top of more general-purpose
resources. (Some refer to these specialized resources as _services_ or
_components_.) We start with hardware and build virtual resources on
top. With storage, the OS starts with disks and memory and creates the
filesystem as a virtual storage resource on top. Databases like
Postgres use the filesystem to create another virtual storage resource
to handle use cases not met by the filesystem. Datomic uses other
databases like Cassandra or DynamoDB as its storage layer. Browsers
create their own virtual environments and introduce new resources like
local storage and cookies.

For computation, the OS introduces processes and threads as virtual
resources representing and organizing program execution. Erlang
creates an environment with a process model that's dramatically
different from the underlying OS's. Same deal with Clojure's
`core.async`, which introduces the _communicating sequential
processes_ computation model. It's a virtual model defined by Clojure
macros, "compiled" to core clojure, then compiled to JVM bytecode (or
JavaScript!), which then has to be executed by operating system
processes.

Interfaces follow the same pattern: on the visual display side, the OS
paints to monitors, applications paint to their own virtual canvas,
browsers are applications which introduce their own resources (the DOM
and `<canvas>`), and React introduces a virtual DOM. Emacs is an
operating system on top of the operating system, and it provides
windows and frames.

Resources manage their own _entities_: in a database, entities could
include tables, rows, triggers, and sequences. Filesystem entities
include directories and files. A GUI manages windows, menu bars, and
other components.

(I realize that this description of _resource_ is not the kind of
airtight, axiomatic, comprehensive description that programmers like.
One shortcoming is that the boundary between resource and application
is pretty thin: Postgres is an application in its own right, but from
the perspective of a Rails app it's a resource. Still, hopefully my
use of _resource_ is clear enough that you nevertheless understand
what the f I'm talking about when I talk about resources.)

Coordinating these resources is inherently complex. Hell, coordinating
anything is complex. I still remember the first time I got smacked in
the face with a baseball in little league thanks to a lack of
coordination. There was also a time period where I, as a child, took
tae kwon do classes and frequently ended up sitting with my back
against the wall with my eyes closed in pain because a) my mom for
some reason refused to buy me an athletic cup and b) I did not possess
the coordination to otherwise protect myself during sparring.

When building a product, you have to decide how to create, validate,
secure, and dispose of resource entities; how to convey entities from
one resource to another; and how to deal with issues like timing (race
conditions) and failure handling that arise whenever resources
interact, all without getting hit in the face. Rails, for instance,
was designed to coordinate browsers, HTTP servers, and databases. It
had to convey user input to a database, and also retrieve and render
database records for display by the user interface, via HTTP requests
and responses.

There is no obvious or objectively correct way to coordinate these
resources. In Rails, HTTP requests would get dispatched to a
Controller, which was responsible for interacting with a database and
making data available to a View, which would render HTML that could be
sent back to the browser.

You don't _have_ to coordinate web app resources using the
Model/View/Controller (MVC) approach Rails uses, but you do have to
coordinate these resources _somehow_. These decisions involve making
tradeoffs and imposing constraints to achieve a balance of
extensibility (creating a system generic enough for new resources to
participate) and power (allowing the system to fully exploit the
unique features of a specific resource).

This is a very difficult task even for experienced developers, and the
choices you make could have negative repercussions that aren't
apparent until you're heavily invested in them. With Rails, for
instance, ActiveRecord (AR) provided a good generic abstraction for
databases, but early on it was very easy to produce extremely
inefficient SQL, and sometimes very difficult to produce efficient
SQL. You'd often have to hand-write SQL, eliminating some of the
benefits of using AR in the first place.

For complete beginners, the task of making these tradeoffs is
impossible because doing so requires experience. Beginners won't even
know that it's necessary to make these decisions. At the same time,
more experienced developers would prefer to spend their time and
energy solving more important problems.

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
could have been represented as Ruby hashes (a primitive akin to a JSON
object), which might have made them more portable while making it more
difficult to concisely express database operations like `save` and
`destroy`. The abstraction also responds to `find`, `create`,
`update`, and `destroy`. It calls your application's code via
lifecycle callback methods like `before_validation`. Frameworks add
value by identifying these lifecycles and providing interfaces for
them when they're absent from the underlying resource.

You already know this, but it bears saying: abstractions let us code
at a higher level. Framework abstractions handle the concerns that are
specific to resource management, letting us focus on building
products. Designed well, they enable loose coupling.

Nothing exemplifies this better than the massively successful _file_
abstraction that the UNIX framework introduced. We're going to look at
in detail because it embodies design wisdom that can help us
understand what makes a good framework.

The core file functions are `open`, `read`, `write`, and
`close`. Files are represented as sequential streams of bytes, which
is just as much a choice as ActiveRecord's choice to use Ruby
objects. Within processes, open files are represented as _file
descriptors_, which are usually a small integer. The `open` function
takes a path and returns a file descriptor, and `read`, `write`, and
`close` take a file descriptor as an argument to do their work.

Now here's the amazing magical kicker: _file_ doesn't have to mean
_file on disk_. Just as Rails implements the ActiveRecord abstraction
for MySQL and Postgres, the OS implements the file abstraction for
**pipes**, terminals, and other resources, meaning that your programs
can write to them using the same system calls as you'd use to write
files to disk - indeed, from your program's standpoint, all it knows
is that it's writing to a file; it doesn't know that the "file" that a
file descriptor refers to might actually be a pipe.

> Exercise for the reader: write a couple paragraphs explaining
> precisely the design choices that enable this degree of loose
> coupling. How can these choices help us in evaluating and designing
> frameworks?

This design is a huge part of UNIX's famed simplicity. It's what lets
us run this in a shell:

```bash
# list files in the current directory and perform a word count on the output
ls | wc
```

The shell interprets this by launching an `ls` process. Normally, when
a process is launched it creates three file descriptors (which,
remember, represent open files): `0` for `STDIN`, `1` for `STDOUT`,
and `2` for `STDERR`, and the shell sets each file descriptor to refer
to your terminal (terminals can be files!! what!?!?). Your shell sees
the pipe, `|`, and sets `ls`'s `STDOUT` to the pipe's `STDIN`, and the
pipe's `STDOUT` to `wc`'s `STDIN`. The pipe links processes' file
descriptors, while the processes get to read and write "files" without
having to know what's actually on the other end. No joke, every time I
think of this I get a little excited tingle at the base of my
spine because I am a:

<iframe width="560" height="315" src="https://www.youtube.com/embed/IRsPheErBj8" frameborder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>

This is why file I/O is referred to as _the universal I/O model_. I'll
have more to say about this in the next section, but I share it here
to illustrate how much more powerful your programming environment can
be if you find the right abstractions. The file I/O model still
dominates decades after its introduction, making our lives easier
_without our even having to understand how it actually works_.

The canonical first exercise any beginner programmer performs is to
write a program that prints out, _Wassup, homies?_. This program makes
use of the file model, but the beginner doesn't have to even know that
such a thing exists. This is what a good framework does. A
well-designed framework lets you easily get started building simple
applications, without preventing you building more complicated and
useful ones as you learn more.

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
"languages" spoken by resources into one or more common languages that
are easy to understand and efficient, while also ensuring
extensibility and composability. Frameworks also do some of the work
of ensuring resilience. This usually entails:

* Establishing naming and addressing conventions
* Establishing conventions for how to structure content
* Introducing communication brokers
* Handling communication failures (the database is down! that file
  doesn't exist!)

One example many people are familiar with is the HTTP stack, a
"language" used to communicate between browser and server resources:

* HTTP structures content (request headers and request body as text)
* TCP handles communication failures
* IP handles addressing

#### Conventions

The file model is a "common language", and the OS uses device drivers
to translate between between the file model and whatever local
language is spoken by hardware devices. It has naming and addressing
conventions, letting you specify files on the filesystem using
character strings separated by slashes that it translates to an
internal inode (a data structure that stores file and directory
details, like ownership and permissions). We're so used to this that
it's easy to forget it's a convention; *nix systems could have been
designed so that you had to refer to files using a number or a
UUID. The file descriptors I described in the last section are also a
convention.

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
with XML instead of byte streams! Hoo boy, what a crazy world that
would be. Having a simple, universal communication system makes it
extremely easy for new resources to participate without having to be
directly aware of each other. It allows us to easily compose command
line tools. It allows one program to write to a log while another
reads from it. In other words, it enables loose coupling and all the
attendant benefits.

#### Communication Brokers

_Globally addressable communication brokers_ (like the filesystem, or
Kafka queues, or databases) are essential to enabling composable
systems. _Global_ means that every resource can access
it. _Addressable_ means that the broker maintains identifiers for
entities independently of its clients, and it's possible for clients
to specify entities using those identifiers. _Communication broker_
means that the system's purpose is to convey data from one resource to
another, and it has well-defined semantics: a queue has FIFO
semantics, the file system has update-in-place semantics, etc.

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
sometimes that direct communication is handled magically. (I seem to
recall that Rails worked with this way, with tight coupling between
Controller and Views and a lack of options for conveying Controller
data to other parts of the system). If someone wants to introduce new
abstractions, they have to untangle all the magic and hook deep into
the framework's internals, using -- or even patching! -- code that's
meant to be private.

I remember running into this with Rails back when MongoDB was
released; the _document database_ resource was sufficiently different
from the _relational database resource_ that it was pretty much
impossible for MongoDB to take part in the ActiveRecord abstraction,
and it was also very difficult to introduce a new data store
abstraction that would play well with the rest of the Rails ecosystem.

For a more current example, a frontend framework might identify the
form as a resource, and create a nice abstraction for it that handles
things like validation and the submission lifecycle. If the form
abstraction is written in a framework that has no communication broker
(like a global state container), then it will be very difficult to
meet the common use case of using a form to filter rows in a table
because there's no way for the code that renders table data to access
the form inputs' values. You might come up with some hack like
defining handlers for exporting the form's state, but doing this on an
ad-hoc basis results in confusing and brittle code.

By contrast, the presence of a communication broker can make life much
easier. In the Clojure world, the React frameworks
[re-frame](https://github.com/Day8/re-frame/) and
[om.next](https://github.com/omcljs/om) have embraced global state
atoms, a kind of communication broker similar to the filesystem (atoms
are an in-memory storage mechanism). They also both have well defined
communication protocols. I'm not very familiar with
[Redux](https://redux.js.org/) but I've heard tell that it also has
embraced a global, central state container.

If you create a form abstraction using re-frame, it's possible to
track its state in a global state atom. It's further possible to
establish a naming convention for forms, making it easier for other
participants to look up the form's data and react to it. (Spoiler
alert: the framework I've been working on does this!)

Communication systems are fundamental. Without them, it's difficult to
build anything but the simplest applications. By providing
communication systems, frameworks relieve much of the cognitive burden
of building a program. By establishing communication standards,
frameworks make it possible for developers to create composable tools,
tools that benefit everybody who uses that framework. Standards make
infrastructure possible, and infrastructure enables productivity.

In this section I focused primarily on the file model because it's
been so successful and I think we can learn a lot from it. Other
models include event buses and message queues. I'm not going to write
about these because I'm not made of words, ok?!?

### Environments

Frameworks are built to coordinate resources within a particular
_environment_. When we talk about desktop apps, web apps, single page
apps, and mobile apps, we're talking about different
environments. From the developer's perspective, environments are
distinguished by the resources that are available, while from the
user's perspective different environments entail different usage
patterns and expectations about distribution, availability, licensing,
and payment.

As technology advances, new resources become available (the Internet!
databases! smart phones! powerful browsers! AWS!), new environments
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
* So that programmers can focus on writing the business logic that's
  specific to their product

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

If you accept my assertion that an operating system is a framework,
then you can consider any program which communicates via one of the
OS's communication systems (sockets, the file model, etc) to be an
extension of the framework. Postgres is a framework extension that adds
an RDBMS resource. statsd is an extension that adds a monitoring
resource.

Similarly, Rails makes it possible for developers to identify
specialized resources and extend the framework to easily support
them. One of the most popular and powerful is
[Devise](https://github.com/plataformatec/devise), which coordinates
Rails resources to introduce a new user authentication resource. Just
as using Postgres is usually preferable to rolling your own database,
using Devise is usually preferable to rolling your own authentication
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
products: not only does a dev have to learn the language's syntax
and paradigms, she has to figure out how to perform the complex task
of abstracting and coordinating resources using the language's
paradigms. If your goal is to create a mass-market product, choosing a
language that doesn't have frameworks for your target environments is
a risky choice.

Finally, frameworks become a base layer that you can create tooling
for. The introduction of the filesystem made it possible for people to
write tools that easily create and manipulate files. Rails's
abstractions made it easy to generate code for creating a new database
table, along with an entire stack - model, view, controller - for
interacting with it.

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
share what they're afraid of. The site would use their IP address to
come up with some geo coordinates and use Google Maps to display a
global fear map. A lot of people were afraid of bears.

Frameworks let you focus on the fun parts of building an app. They let
you release an idea, however dumb, more quickly.

### Frameworks Help Beginners

Frameworks help beginners by empowering them to build real,
honest-to-god running applications that they can show to their friends
and even make money with, without having to fully understand or even
be aware of all the technology they're using. Being able to conjure up
a complete creation, no matter how small or ill-made, is the very
breath of wonder and delight. (I don't know exactly what this means,
but I like how it sounds!)

There's a kind of thinking that says frameworks are bad because they
allow beginners to make stuff without having to know how it all
works. ActiveRecord is corrupting the youth, allowing them to build
apps without even knowing how to pronounce _SQL_.

There's another line of thinking that says it's bad to try to make
things easier for beginners. It's somehow virtuous for people to
struggle or suffer for the sake of learning.

Hogwash. Fiddlefaddle. Poppycock. Joy beats suffering every time, and
making learning more joyful allows more people to reap the benefits of
whatever tool or product you've created.

I am a photographer. I have a professional camera, and I know how to
use it. Some of my photos require a fair amount of technical knowledge
and specialized equipment:

![tea](/assets/images/posts/why-programmers-need-frameworks/tea.jpg)

This isn't something you can create with a camera phone, yet somehow
I'm able to enjoy myself and my art without complaining that
point-and-shoot cameras exist and that people like them.

Novices benefit greatly from expert guidance. I don't think you can
become a master photographer using your phone's camera, but with the
phone's "guidance" you can take some damn good photos and be proud of
them. And if you do want to become a master, that kind of positive
feedback and sense of accomplishment will give you the motivation to
stick with it and learn the hard stuff. Frameworks provide this
guidance by creating a safe path around all the quicksand and pit
traps that you can stumble into when creating an app. Frameworks help
beginners. This is a feature, not a bug.

## A Clojure Framework

Frameworks are all about managing the complexity of coordinating
resources. Well, guess what: Managing Complexity is Clojure's middle
name. Clojure "Managing Complexity" McCarthy-Lisp. Personally, I want
a single-page app (SPA) framework, and there are many aspects of
Clojure's design and philosophy that I think will make it possible to
create one that seriously kicks ass. I'll give just a few examples.

First, consider how Linux tools like `sed` and `awk` are
text-oriented. Developers can add additional structure to text by
formatting it as JSON or YAML, and those text-processing tools can
still work the structured text.

In the same way, Clojure's emphasis on simple data structures means
that we can create specialized structures to represent forms and ajax
request, and tools to process those structures. If we define those
structures in terms of maps and vectors, though, we'll still be able
to use a vast ecosystem of functions for working with those simpler
structures. In other words, creating specialized structures does not
preclude us from using the tools built for simpler structures, and
this isn't the case for many other languages.

Second, Clojure's abstraction mechanisms (prototypes and multimethods)
are extremely flexible, making it easy for us to implement
abstractions for new resources as they become available.

Third, _you can use the same language for the frontend and backend!!!_
Not only that, Transit allows the two to effortlessly
communicate. This eliminates an entire class of coordination problems
that frameworks in other languages have to contend with.

In my opinion, the Clojurian stance that frameworks are more trouble
than they're worth is completely backwards: Clojure gives us the
foundation to build a completely kick-ass framework! One that's simple
_and_ easy. One can dream, right?

My ambition in building a SPA framework is to empower current and
future Clojure devs to get our ideas into production _fast_. I want us
to be able to spend more time on the hard stuff, the fun stuff, the
interesting stuff. And I want us to be able to easily ship with
confidence.

The framework I'm building is built on top of some truly amazing
libraries, primarily Integrant, re-frame, and Liberator. Integrant
introduces a _component_ abstraction and handles the start/stop
lifecycle of an application. re-frame provides a filesystem and
communication broker for the frontend. Liberator introduces a standard
model for handling HTTP requests.

If my framework is useful at all it's because the creators of those
tools have done all the heavy lifting. My framework introduces more
resources and abstractions specific to creating single-page apps. For
example, it creates an abstraction for wrapping AJAX requests so that
you can easily display activity indicators when a request is
active. It creates a form abstraction that handles all the plumbing of
handling input changes and dispatching form submission, as well the
entire form lifecycle of _fresh_, _dirty_, _submitted_, _invalid_,
_succeeded_, etc. It imposes some conventions for organizing data.

As I mentioned, the framework is not quite ready for public
consumption yet becaause there's still a lot of churn while I work out
ideas, and because there's basically no documentation, but I hope to
release it in the near future.

If you'd like to see a production app that uses the framework,
however, I invite you to check out [Grateful
Place](https://gratefulplace.com), a community site for people who
want to support each other in growing resilience, peace, and joy by
practicing compassion, gratitude, generosity, and other positive
values. By joining, you're not just helping yourself, you're helping
others by letting them know that you support them and share their
values.

Please click around and look at the snazzy loading animations. And if
you feel so moved, please do join! I _love_ getting to interact with
people in that context of mutual support for shared values. One of the
only things I care about more than Clojure is helping people develop
the tools to navigate this crazy-ass world :D

In the mean time, I'll keep working on getting this framework ready
for public consumption. Expect another blawg article sharing some
details on how Grateful Place is implemented. Then, eventually,
hopefully, an actual announcement for the framework itself :)

If you don't want to wait for my slow butt, then check out some ofthe
amazing Clojure tools that already exist:

* [Luminus](http://www.luminusweb.net)
* [Fulcro](http://book.fulcrologic.com/) which probably does
  everything I want my framework to, only better
* [re-frame](https://github.com/Day8/re-frame/) remains my favorite
  frontend framework
* [duct](https://github.com/duct-framework/duct) is great but its docs
  aren't that great yet

(Sorry if I neglected your amazing Clojure tool!)

Thanks to the following people who read drafts of this article and
helped me develop it:

* Mark Bastian
* [Dmitri Sotnikov aka @yogthos](https://twitter.com/yogthos)
* Sergey Shvets
* Kenneth Kalmer
* Sean whose last name I don't know
* Tom Brooke
* Patrick whose last name I don't know (update: It's Patrick French!)
* Fed Reggiardo
* Vincent Raerek
* Ernesto de Feria
* Bobby Towers
* Chris Oakman
* The TriClojure meetup
