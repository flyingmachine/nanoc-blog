---
title: "How Clojure Babies are Made: What Leiningen Is"
created_at: Apr 5 9:23:00 -0500 2013
kind: article
categories: programming, clojure
summary: The Clojure packaging ecosystem
additional_stylesheets:
  - pygments
---

"Wait &ndash; what the hell is Leiningen?" is a question you've
probably overheard many times in your day-to-day life. You've probably
even asked it yourself. Up until now we've described specific
capabilities that Leiningen has and how those capabilities are
implemented. It can
[build and run your app](/programming/how-clojure-babies-are-made-lein-run),
it [has a trampoline](/programming/lein-run), and so forth.

But it's time to take a step back and get a high-level understanding
of what Leiningen is. It's time to stare deeply into Leiningen's eyes
and say "I see you," like Jake Sully in that Avatar documentary. We'll
do this by giving an overview of the non-coding related tasks you need
to accomplish when building software. Next, we'll describe how
Leiningen helps you accomplish these tasks and compare it to similar
technology, Ruby.

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

For example, the following tools (and more) are part of the Ruby
artifact ecosystem:

* _Ruby Gems_ provides a package specification, the means to
  incorporate gems in your project, and the means to build and publish
  gems
* _[rubygems.org](http://rubygems.org)_ is a central repo for gems
* _Bundler_ provides a layer on top of Ruby Gems, providing dependency
  resolution and gem retrieval
* _Jeweler_ is one of many tools for easing the process of creating
  gemspecs and building gems.

## Leiningen Is a Task Runner with Clojure Tasks Built In

In previous Clojure Baby posts, we've already seen that we can use
Leiningen to build and run Clojure programs. It turns out that
Leiningen also handles the remaining tasks - retrieving packages,
incorporating them in your project, and publishing them.

All of these artifact-related tasks are especially tedious in Clojure
because of its nature as a hosted language. In Ruby, all artifact
tools are written in Ruby itself. In Clojure, you have to deal with

Ultimate, Leiningen is a task runner with a built in set of tasks 


It goes without saying that developers want to spend as little time as
possible cajoling their tools to accomplish the above tasks. The
result is that we end up with a layered set of tools where each layer
attempts to smooth out the warts of the layer beneath it.

For example, in Ruby, the the Ruby Gems library allows you to
incorporate Ruby artifacts (gems) into your project. But for years,
Ruby Gems provided no facility for dependency resolution. Consider the
following scenario:

* Your project requires gem A which depends on version 1.0 of gem C
* Your project requires gem B which depends on version 1.1 of gem C

In this situation, plain old Ruby Gems completely freaks out and
throws an exception. Up until a few years ago, the main tactic that
rubyists had for dealing with this situation was to lock themselves
in a closet and sob quietly. Now we have Bundler, a tool which sits on
top of Ruby Gems and provides sane dependency resolution. QED, the
tools in our artifact ecosystem are layered.

## Task Runners

The topmost layer of artifact ecosystems is usually a task runner.
