---
title: "How Clojure Babies are Made: What Leiningen Is"
created_at: Apr 5 9:23:00 -0500 2013
kind: article
categories: programming, clojure
summary: The Clojure packaging ecosystem
additional_stylesheets:
  - pygments
---

"What the hell is Leiningen?" is a question you've probably overheard
many times in your day-to-day life. You've probably even asked it
yourself. Up until now we've described specific capabilities that
Leiningen has and how those capabilities are implemented. It can
[build and run your app](/programming/how-clojure-babies-are-made-lein-run),
it [has a trampoline](/programming/lein-run), and so forth.

But it's time to take a step back and get a high-level understanding
of what Leiningen is. It's time to stare deeply into Leiningen's eyes
and say "I see you," like Jake Sully in that Avatar documentary. We'll
do this by giving an overview of the non-coding related tasks you need
to accomplish when building software. Next, we'll describe how
Leiningen helps you accomplish these tasks and compare it to similar
tools in Ruby.

This post isn't as nitty-gritty as the previous posts in the Clojure
Babies series, but it will help lay the groundwork for the upcoming
post on packaging. Additionally, I hope it will clarify what a
programming language artifact ecosystem is. This concept is often
overlooked when teaching a programming language, and when it _is_
covered it's not covered in a systematic way. Together, noble-chinned
reader, we will remedy that situation. For our generation and all
generations to come.

## Programming Language Artifact Ecosystems

In order to become proficient at a language, you need to know much
more than just its syntax and semantics. You need to familiarize
yourself with the entire programming language ecosystem, which is
comprised of everything you need in order to build working software in
that language. It can be broken down into at least the following
sub-ecosystems:

* The documentation ecosystem
* The developer community
* The development environment ecosystem (editor support)
* The artifact ecosystem

Right now we only care about the artifact ecosystem. For our purposes,
a programming artifact is a library or executable. Ruby gems, shell
scripts, Java jars, shared libraries, and "HAL9000.exe" are all
programming artifacts.

An artifact ecosystem is the set of tools and services that allow
you to do the following with regard to artifacts:

* Retrieve them from repositories
* Incorporate them in your own project, (possibly) resolving conflicts
* Build them
* Publish them to repositories
* Run them

Tools are often layered on top of each other, one tool smoothing out
the warts of the tools it wraps. For example, the following tools (and
more) are part of the Ruby artifact ecosystem:

* _Ruby Gems_ provides a package specification, the means to
  incorporate gems in your project, and the means to build and publish
  gems
* _[rubygems.org](http://rubygems.org)_ is a central repo for gems
* _Bundler_ provides a layer on top of Ruby Gems, providing dependency
  resolution and gem retrieval
* _Jeweler_ is one of many tools for easing the process of creating
  gemspecs and building gems.

In previous Clojure Baby posts, we've seen that we can use Leiningen
to build and run Clojure programs. It turns out that Leiningen also
handles the remaining tasks - retrieving packages, incorporating them
in your project, and publishing them. It's truly the Swiss Army
Bazooka (I'm going to keep repeating that phrase until it catches on)
of the Clojure artifact ecosystem.

Leiningen is able to handle so many responsibilities because it is, at
heart, a task runner. It just happens to come with an
[excellent set of built-in tasks]((https://github.com/technomancy/leiningen/tree/master/src/leiningen))
for handling Clojure artifacts.

## Leiningen Is a Task Runner with Clojure Tasks Built In

Artifact-related tasks are especially cumbersome in Clojure because of
its nature as a hosted language. In order to really understand the
Clojure artifact ecosystem, you have to learn about Java, the JVM, and
the way Java handles artifacts. Additionally, it seems like handling
this stuff wih Java tools like Maven and Ant is kind of a pain in the
butt so that even if you were doing pure Java development you'd be
pretty sad.

By comparison, all Ruby artifact tools are written in Ruby itself. It
is still a chore to become familiar with the ecosystem, but at least
you're not required to learn a second language.

Leiningen's nature as a task runner gives it the flexibility to glue
together existing pieces of the Clojure/Java ecosystem in a pleasant,
cohesive way.

The only "drawback" to this approach is that sometimes you have no
clue as to what Leiningen is doing. It's difficult to untangle
Leiningen from Java and Clojure. But that's where these articles come
in, right?

## The End

Hopefully, this article has given you a firmer grasp of what Leiningen
is. The idea that Leiningen is a task runner with a powerful set of
built-in tasks designed to aid Clojure artifact management should help
you organize your disparate chunks of Leiningen knowledge.

In the next article, we'll add another chunk of Leiningen khnowledge
by examining the way Leiningen retrieves artifacts from repositories
and incorporates them in your project.

Goodbye for now!

