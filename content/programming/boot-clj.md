---
title: Boot, Clojure Build Tooling
created_at: Tue Jan 5 2015 19:39:00 -0500
kind: article
categories: programming
summary: "Boot provides abstractions for creating Clojure tasks"
draft: true
---

Build tools are known to inspire the entire gamut of emotions from
bored impatience to Homeric rage (I'm looking at you,
Grunt). Personally, I've never given them much thought; they've always
seemed like tedious overhead, an unfortunate necessity for getting
*real work* done.

Recently, though, I've started learning about
[Boot](http://boot-clj.com/), and it's made me believe that build
programming can actually be interesting. This article will explain
Boot's underlying concepts and guide you through writing your first
Boot tasks. If you're interested in using Boot to build projects right
this second, then check out its
[github README](https://github.com/boot-clj/boot) and its
[wiki](https://github.com/boot-clj/boot/wiki).

## Boot's Abstractions

Created by Micha Niskin and Alan Dipert, Boot is a completely
controversy-free addition to the Clojure tooling landscape. On the
surface, it's "merely" a convenient way to build Clojure applications
and run Clojure tasks from the command line. But dig a little deeper
and you'll see that Boot is like the lisped-up lovechild of Git and
Unix, providing abstractions that make it much more pleasant to write
code that exists at the intersection of your operating system and your
application.

Unix provides abstractions that we're all familiar with to the point
of taking them for granted. (I mean, would it kill you to take your
computer out to a nice restaurant once in awhile?) The process
abstraction allows you to reason about programs as isolated units of
logic that can be easily composed into a stream-processing pipeline
through the `STDIN` and `STDOUT` file descriptors. These abstractions
make certain kinds of operations, like text processing, very easy.

Similarly, Boot provides abstractions that make it actually pleasant
to compose independent operations into the kinds of complex,
coordinated operations that build tools end up doing, like converting
Clojurescript into Javascript. Boot's *task* abstraction lets you
easily define units of logic that communicate through *filesets*. The
fileset abstraction keeps track of the evolving build context and it
provides a well-defined, reliable method of task coordination, as
opposed to the ill-defined, ad-hoc task coordination which programmers
have to impose on other build tools.

That's a lot of high-level description, which hopefully is great for
when you want to hook someone's attention, which hopefully I have now
done. But I would be ashamed to leave you with a plateful of
metaphors. Oh no, dear reader; that was only the appetizer. For the
rest of this article you will learn what that word salad means by
building your own Boot tasks. Along the way, you'll discover that
build tools can actually have a conceptual foundation.

## Tasks

Like make, rake, grunt, and other build tools of yore, Boot lets you
define *tasks*. Tasks are

* named operations
* that take command line options
* dispatched by some intermediary program (make, rake, Boot)

Boot provides the dispatching program, `boot`, and a Clojure library
that makes it easy for you to define named operations and their
command line options with the `deftask` macro. So that you can see
what all the fuss is about, it's time to create your first
task. Normally, programming tutorials encourage have you write
something that prints "Hello World", but I like my examples to have
real-world utility, so your task is going to print "My pants are on
fire!", information which is objectively more useful. First,
[install Boot](https://github.com/boot-clj/boot#install), then create
a new directory named `boot-walkthrough`, navigate to that directory,
and finally create a file named `build.boot` and put in this in it:

```clojure
(deftask fire
  "Prints 'My pants are on fire!'"
  []
  (println "My pants are on fire!"))
```

Now run this task with `boot fire`; you should see the message you
wrote printed to your terminal. This demonstrates two out of the three
task components - the task is named (`fire`) and it's dispatched by
`boot`. This is already super cool &ndash; you've essentially created
a Clojure script, standalone Clojure code that you can easily run from
the command line. No `project.clj` or directory structure or
namespaces needed!

Let's extend the example to demonstrate how you'd write command line
options:

```clojure
(deftask fire
  "Announces that something is on fire"
  [t thing     THING str  "The thing that's on fire"
   p pluralize       bool "Whether to pluralize"]
  (let [verb (if pluralize "are" "is")]
    (println "My" thing verb "on fire!")))
```

Try running the task like so:

```bash
boot fire -t heart
# => My heart is on fire!

boot fire -t logs -p
# => My logs are on fire!
```

In the first instance, either you're newly in love or you need to be
rushed to the emergency room. In the second, you are a boy scout
awkwardly exclaiming your excitement over meeting the requirements for
a merit badge. In both instances, you were able to easily specify
options for the task.

This refinement of the `fire` task introduced two command line
options, `thing` and `pluralize`. These options are defined using the
options DSL. In the case of `thing`, `t` specifies the option's short
name and `thing` specifies the long name. `THING` is a little
complicated, and I'll get to it in a second. `str` specifies the
option's type, and Boot uses that both to validate the argument and
convert it. `"The thing that's on fire"` is the documentation for the
option. You can view a task's documentation with `boot task-name -h`:

```bash
boot fire -h
# Announces that something is on fire
# 
# Options:
#   -h, --help         Print this help info.
#   -t, --thing THING  Set the thing that's on fire to THING.
#   -p, --pluralize    Whether to pluralize
```

Pretty groovy! Boot makes it very, very easy to write code that's
meant to be invoked from the command line.

Now, about `THING`. `THING` is an *optarg* and it indicates that this
option expects an argument. You don't have to include an optarg when
you're defining an option (notice that the `pluralize` option has no
optarg). The optarg doesn't have to correspond to the full name of the
option; you could replace `THING` with `BILLY_JOEL` or whatever you
want and the task would work the same. Finally, you can also designate
[complex options](https://github.com/boot-clj/boot/wiki/Task-Options-DSL#complex-options)
using the optarg. (That link will take you to Boot's documentation on
the subject.) Basically, complex options allow you to specify that
option arguments should be treated as as maps, sets, vectors, or even
nested collections. It's pretty powerful.

Boot provides you with all the tools you could ask for in building
command-line interfaces with Clojure. And you've only just started
learning about it!

## The REPL

Boot comes with a good number of useful built-in tasks, including a
REPL task; run `boot repl` to fire up that puppy. The Boot REPL is
similar to Leiningen's in that it handles loading your project code so
that you can play around with it. You might not think this applies to
the project you've been writing because you've only written tasks, but
you can actually run tasks in the REPL (I've left out the
`boot.user=>` prompt):

```clojure
(fire "-t" "NBA Jam guy")
; My NBA Jam guy is on fire!
;=> nil

(fire "-p" "-t" "NBA Jam guys")
; My NBA Jam guys are on fire!
;=> nil
```

And of course, you can also use `deftask` in the REPL &ndash; it's just
Clojure, after all. The takeaway is that Boot lets you interact with
your tasks as Clojure functions, because that's what they are.

## Composition and Coordination



## Everything else

The point of this article was to explain the concepts behind Boot. It
has a bunch of features, like `set-env!` and `task-options!` that make
life easier when you're actually using it. If Boot tickles your fancy,
check out its (README)[https://github.com/boot-clj/boot] for more info
on real-world usage. Also, its
[wiki](https://github.com/boot-clj/boot/wiki) provides top-notch
documentation. Have fun!
