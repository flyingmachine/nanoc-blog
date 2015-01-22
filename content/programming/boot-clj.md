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
programming can actually be interesting. Created by Micha Niskin and
Alan Dipert, Boot is a completely controversy-free addition to the
Clojure tooling landscape. On the surface, it's "merely" a convenient
way to build Clojure applications and run Clojure tasks from the
command line. But dig a little deeper and you'll see that Boot is like
the lisped-up lovechild of Git and Unix, providing abstractions that
make it much more pleasant to write code that exists at the
intersection of your operating system and your application.

In the same way that the Unix process abstraction allows you to reason
about programs as isolated units of logic that can be easily composed
into a pipeline through the `STDIN` and `STDOUT` file descriptors,
Boot provides the Task abstraction to define units of logic with
the Fileset, another abstraction, as the communication medium.

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
`boot`. Let's extend it to demonstrate how you'd write command line
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
