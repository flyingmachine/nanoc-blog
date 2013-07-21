---
title: "How Clojure Babies are Made: What Leiningen Is"
created_at: Apr 7 9:23:00 -0500 2013
kind: article
categories: programming, clojure
summary: "It's time to take a step back and get a high-level understanding of what Leiningen is. It's time to stare deeply into Leiningen's eyes and say \"I see you,\" like Jake Sully in that Avatar documentary."
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
Babies series, but it will help lay the groundwork for an upcoming
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

Other languages have their own tools. Java has Maven, PHP has Pear or
whatever. Artifact management is a common need across languages.

In previous Clojure Baby posts, we've seen that we can use Leiningen
to build and run Clojure programs. It turns out that Leiningen also
handles the remaining tasks - retrieving packages, incorporating them
in your project, and publishing them. It's truly the Swiss Army
Bazooka (I'm going to keep repeating that phrase until it catches on)
of the Clojure artifact ecosystem.

But why is it that in Ruby you need an entire constellation of tools,
while in Clojure you only need one?

## Leiningen Is a Task Runner with Clojure Tasks Built In

Leiningen is able to handle so many responsibilities because it is, at
heart, a task runner. It just happens to come with an
[excellent set of built-in tasks](https://github.com/technomancy/leiningen/tree/master/src/leiningen)
for handling Clojure artifacts. (Incidentally, this is probably where
Leiningen's name came from. "Leiningen Versus the Ants" is a short
story where the protagonist fights ants. Ant is a Java build tool that
evidently is unpleasant to use for Clojure builds.) By comparison,
Ruby's Rake is also a task runner used by many of Ruby's artifact
tools, but Rake provides no built-in tasks for working with Ruby
artifacts.

"Task runner" is a little bit ambiguous, so let's break it down.
Ultimately, all Leiningen tasks are just Clojure functions. However,
in [previous posts](/programming/how-clojure-babies-are-made-lein-run)
we've seen how fun it is to try and run Clojure functions from the
command line. In case you need a short refresher: it's not fun at all!

Leiningen allows the Clojure party to remain fun by serving as an
adapter between the CLI and Clojure. It takes care of the plumbing
required for you to run a Clojure function. Whether the function is
provided by your project, by Leiningen's built-in tasks, or by a
[Leiningen plugin](https://github.com/technomancy/leiningen/blob/master/doc/PLUGINS.md),
Leiningen does everything necessary to get the function to run. In a
way, Leiningen's like an attentive butler who quietly and competently
takes care of all your chores so that you can focus on your true
passions, like knitting sweaters for gerbils or whatever.

This manner of executing code was foreign to me when I first came to
Clojure. At that time I had mostly coded in Ruby and JavaScript, and I
had a decent amount of experience in Objective C. Those languages
employ two different paradigms of code execution.

Ruby and Javascript, being scripting languages, don't require
compilation and execute statements as they're encountered. Objective C
requires compilation and always starts by executing a `main` method.
With Leiningen, Clojure has achieved an amalgamation of the two
paradigms by allowing you to easily run arbitrary functions with a
compiled language.

## The End

Hopefully, this article has given you a firmer grasp of what Leiningen
is. The idea that Leiningen is a task runner with a powerful set of
built-in tasks designed to aid Clojure artifact management should help
you organize your disparate chunks of Leiningen knowledge.

In the next article, we'll add another chunk of Leiningen knowledge
by examining the way Leiningen retrieves artifacts from repositories
and incorporates them in your project.

Goodbye for now!

## Shout Outs

Thanks to [Pat Shaughnessy](http://patshaughnessy.net/) and
[technomancy](http://technomancy.us/) for reviewing this article.
technomancy provided the line "Leiningen is an adapter between the CLI
and Clojure", which really helped!
