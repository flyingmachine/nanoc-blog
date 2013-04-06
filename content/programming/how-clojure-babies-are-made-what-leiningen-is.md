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
more than just its syntax in semantics. You need to familiarize
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
programming artifacts. We could probably spend all day splitting hairs
about what is and is not an artifact, but this is my blog and I'm
going to define the term how I feel, dag nabbit. If the definition is
unclear then hopefully it will become clear through usage.

An artifact ecosystem is the set of tools and services that allow
you to do the following with regard to artifacts:

* Retrieve them from repositories
* Incorporate them in your own program, (possibly) resolving conflicts
* Build them
* Publish them to repositories
* Run them

It goes without saying that developers want to spend as little time as
possible cajoling their tools to accomplish the above tasks. The
result is that we end up with a layered set of tools where each layer
attempts to smooth out the warts of the layer beneath it.

For example, in Ruby, the "gem" program can retrieve gems, build them,
and publish them. But the process of creating a gem is a pain in the
booty, hence we have a cornucopia of tools like Jeweler and Hoe. And,
though we all became overwhelmed with joy it when our Ruby programs
stopped working because of a dependency conflict, Bundler has sadly
robbed us of that particular source of endorphins.

