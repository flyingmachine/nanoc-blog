---
title: Boot, the Fancy New Clojure Build Framework
created_at: Mon Feb 15 2015 09:18:00 -0500
kind: article
categories: programming
summary: "Build tools are known to inspire the entire gamut of emotions from
bored impatience to Homeric rage (I'm looking at you,
Grunt). Personally, I've never given them much thought; they've always
seemed like tedious overhead, an unfortunate necessity for getting
<em>real</em> work done. Boot is different."
additional_stylesheets:
  - pygments
---

Build tools are known to inspire the entire gamut of emotions from
bored impatience to Homeric rage (I'm looking at you,
Grunt). Personally, I've never given them much thought; they've always
seemed like tedious overhead, an unfortunate necessity for getting
*real* work done.

Recently, though, I've started learning about
[Boot](http://boot-clj.com/), and I've found that build programming
can actually be interesting. This article will explain Boot's
underlying concepts and guide you through writing your first Boot
tasks. If you're interested in using Boot to build projects right this
second, then check out its
[github README](https://github.com/boot-clj/boot) and its
[wiki](https://github.com/boot-clj/boot/wiki).

## Boot's Abstractions

Created by Micha Niskin and Alan Dipert, Boot is a completely
controversy-free addition to the Clojure tooling landscape. On the
surface, it's "merely" a convenient way to build Clojure applications
and run Clojure tasks from the command line. But dig a little deeper
and you'll see that Boot is like the lisped-up lovechild of Git and
Unix in that it provides abstractions that make it much more pleasant
to write code that exists at the intersection of your operating system
and your application.

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
;; pass arguments as flags
(fire "-t" "NBA Jam guy")
; My NBA Jam guy is on fire!
;=> nil

;; or as keywords
(fire :thing "NBA Jam guy")
; My NBA Jam guy is on fire!
;=> nil

(fire "-p" "-t" "NBA Jam guys")
; My NBA Jam guys are on fire!
;=> nil

(fire :pluralize true :thing "NBA Jam guys")
; My NBA Jam guys are on fire!
;=> nil
```

And of course, you can also use `deftask` in the REPL &ndash; it's just
Clojure, after all. The takeaway is that Boot lets you interact with
your tasks as Clojure functions, because that's what they are.

## Composition and Coordination

If what you've seen so far was all that Boot had to offer, it'd be a
pretty swell tool, though not very different from other build
tools. One thing that sets Boot apart, though, is how it lets you
compose tasks. For comparison's sake, here's an example Rake
invocation (Rake is the premier Ruby build tool):

```
rake db:create db:migrate db:seed
```

In case you were wondering, this will create a database, run
migrations on it, and populate it with seed data when run in a Rails
project. What's worth noting, however, is that Rake doesn't provide
any way for these tasks to communicate with each other. Specifying
multiple tasks is just a convenience, saving you from having to run
`rake db:create; rake db:migrate; rake db:seed`. If you want to access
the result of Task A within Task B, the build tool doesn't help you;
you have to manage that coordination yourself. Usually, you'll do this
by shoving the result of Task A into a special place on the
filesystem, and then making sure Task B reads that special place.
This looks like programming with mutable, global variables, and it's
just as brittle.

## Handlers and Middleware

Boot addresses this problem by treating tasks as *middleware
factories*. If you're familiar with
[Ring](https://github.com/ring-clojure/ring), Boot's tasks work very
similarly; feel free to skip to the next section.  If you're not
familiar with the concept of middleware, then allow me to explain!
First, the term *middleware* refers to a set of *conventions* that
programmers adhere to so that they can *flexibly create
domain-specific function pipelines*. That's pretty dense, so let's
un-dense it. I'll go over the *flexible* part in this section, and
cover *domain-specific* in the next.

To understand how the middleware approach differs from run-of-the-mill
function composition, here's an example of composing everyday
functions:

```clojure
(def strinc (comp str inc))
(strinc 3)
; => "4"
```

There's nothing interesting about this function composition. This
function composition is so unremarkable that it strains my abilities
as a writer to try and actually say anything about it. There are two
functions, each doing its own thing, and now they've been been
composed into one. Whoop-dee-do!

Middleware introduce an extra step to function composition, and this
gives you more flexibility in defining your function
pipeline. Suppose, in the example above, that you wanted to return `"I
don't like the number X"` for arbitrary numbers, but still return the
stringified number for everything else. Here's how you could do that:

```clojure
(defn whiney-str
  [rejects]
  {:pre [(set? rejects)]}
  (fn [x]
    (if (rejects x)
      (str "I don't like " x)
      (str x))))

(def whiney-strinc (comp (whiney-str #{2}) inc))
(whiney-strinc 1)
; => "I don't like 2 :'("
```

Now let's take it one step further. What if you want to decide whether
or not to call `inc` in the first place? Here's how you could do that:

```clojure
(defn whiney-middleware
  [next-handler rejects]
  {:pre [(set? rejects)]}
  (fn [x]
    (if (= x 1) ; ~1~
      "I'm not going to bother doing anything to that"
      (let [y (next-handler x)]
        (if (rejects y)
          (str "I don't like " y " :'(")
          (str y))))))

(def whiney-strinc (whiney-middleware inc #{3}))
```

Here, instead of using `comp` to create your function pipeline, you
pass the next function in the pipeline as the first argument to the
middleware function. In this case, you're passing `inc` as the first
argument to `whiney-middleware`. `whiney-middleware` then returns an
anonymous functions which closes over `inc` and has the ability to
choose whether to call it or not. You can see this at `~1~`.

We say that middleware take a *handler* as their first argument, and
return a handler. In the example above, `whiney-middleware` takes a
handler as its first argument, `inc` here, and it returns another
handler, the anonymous function with `x` as its only
argument. Middleware can also take extra arguments, like `rejects`,
that act as configuration. The result is that the handler returned by
the middleware can behave more flexibly (thanks to configuration) and
it has more control over the function pipeline (because it can choose
whether or not to call the next handler).

## Tasks are Middleware Factories

Boot takes this pattern one step further by separating middleware
configuration from handler creation. First, you create a function
which takes *n* many configuration arguments. This is the *middleware
factory* and it returns a middleware function. The middleware function
expects one argument, the next handler, and it returns a handler, just
like in the example above. Here's a whiney middleware factory:

```clojure
(defn whiney-middleware-factory
  [rejects]
  {:pre [(set? rejects)]}
  (fn [handler]
    (fn [x]
      (if (= x 1)
        "I'm not going to bother doing anything to that"
        (let [y (handler x)]
          (if (rejects y)
            (str "I don't like " y " :'(")
            (str y)))))))
            
(def whiney-strinc ((whiney-middleware-factory #{3}) inc))
```

As you can see, it's nearly identical to the previous example. The
change is that the topmost function, `whiney-middleware-factory`, now
only accepts one argument, `rejects`. It returns an anonymous
function, the middleware, which expects one argument, a handler. The
rest of the code is the same.

In Boot, tasks can act as middleware factories. In fact, they usually
do, I just didn't present them that way above in order to keep things
simple. To show this, let's split the `fire` task into two tasks:
`what` and `fire`. `what` will let you specify an object and whether
it's plural, and `fire` will announce that it's on fire. This is
great, modular software engineering because it allows you to add other
tasks like `gnomes`, to announce that a thing is being overrun with
gnomes, which is just as objectively useful. (Exercise for the reader:
create the `gnome` task.)

```clojure
(deftask what
  "Specify a thing"
  [t thing     THING str  "An object"
   p pluralize       bool "Whether to pluralize"]
  (fn middleware [next-handler]
    (fn handler [_]
      (next-handler {:thing thing :pluralize pluralize}))))

(deftask fire
  "Announce a thing is on fire"
  []
  (fn middleware [next-handler]
    (fn handler [thing-map]
      (let [updated-thing-map (next-handler thing-map)
            verb (if (:pluralize thing-map) "are" "is")]
        (println "My" (:thing thing-map) verb "on fire!")))))
```

Here's how you'd run this on the command line:

```
boot what -t "pants" -p -- fire
```

And here's how you'd run it in the REPL:

```clojure
(boot (what :thing "pants" :pluralize true) (fire))
```

Wait a minute, what's that `boot` call doing there? In Micha's words,
"The `boot` macro takes care of setup and cleanup (creating the
initial fileset, stopping servers started by tasks, things like
that). Tasks are functions so you can call them directly, but if they
use the fileset they will fail unless you call them via the boot
macro." Wait a minute, what's a fileset?

## Filesets

I mentioned earlier that middleware are for creating *domain-specific*
function pipelines. All that means is that each handler expects to
receive domain-specific data, and returns domain-specific data. With
Ring, for example, each handler expects to receive a *request map*
representing the HTTP request. This might look something like:

```clojure
{:server-port 80
 :request-method :get
 :scheme :http}
```

Each handler can choose to modify this request map in some way before
passing it on to the next handler, say by adding a `:params` key with
a nice Clojure map of all query string and POST parameters. Ring
handlers return a *response map*, which consists of the keys
`:status`, `:headers`, and `:body`, and once again each handler can
transform this data in some way before returning it to its parent
handler.

In Boot, each handlers receives and returns a
[*fileset*](https://github.com/boot-clj/boot/wiki/Filesets).  The
fileset abstraction gives you a way to treat files on your filesystem
as immutable data, and this is a great innovation for build tools
because building projects is so file-centric. For example, your
project might need to place temporary, intermediary files on the
filesystem. Usually, with build tools that aren't Boot, these files
get placed in some specially-named place, say,
`project/target/tmp`. The problem with this is that
`project/target/tmp` is effectively a global variable, and other tasks
can accidentally muck it up.

The fileset abstraction works by adding a layer of indirection on top
of the filesystem. Let's say Task A creates File X and tells the
fileset to store it. Behind the scenes, the fileset stores the file in
an anonymous, temporary directory. The fileset then gets passed to
Task B, and Task B modifies File X and asks the fileset to store the
result. Behind the scenes, a new file, File Y, is created and stored,
but File X remains untouched. In Task B, an updated fileset is
returned. This is the equivalent of doing `assoc-in` with a map; Task
A can still access the original fileset and the files it references.

The mechanics of working with filesets are all explained in
[the fileset wiki](https://github.com/boot-clj/boot/wiki/Filesets),
but I hope this gives a good conceptual overview!

## Everything else

The point of this article was to explain the concepts behind
Boot. However, it also has a bunch of features, like `set-env!` and
`task-options!` that make life easier when you're actually using
it. It does *amazing magical things* like *providing classpath isolation
so that you can run multiple projects using one JVM* and *letting you
add new dependencies to your project without having to restart your
REPL*.  If Boot tickles your fancy, check out its
[README](https://github.com/boot-clj/boot) for more info on real-world
usage. Also, its [wiki](https://github.com/boot-clj/boot/wiki)
provides top-notch documentation.

If you're new to Clojure, then check out
[Clojure for the Brave and True](http://www.braveclojure.com/), an
introduction to the language written by yours truly. Have fun!
