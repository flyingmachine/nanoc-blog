---
title: Boot, Clojure Build Tooling
created_at: Tue Jan 5 2015 19:39:00 -0500
kind: article
categories: programming
summary: "Boot provides abstractions for creating Clojure tasks"
draft: true
---

On the surface, [Boot](http://boot-clj.com/), the new Clojure
ecosystem creation of Micha Niskin and Alan Dipert, is "merely" an
easy-to-use, convenient way to build Clojure applications and run
Clojure tasks from the command line. But if you dig a little deeper,
you'll see that Boot is like the lisped-up philosophical lovechild of
Git and Unix. It goes beyond providing a few conveniences; it asks,
"What is the essence of a build tool? What kinds of behaviors should
it support?" The result is a platform that provides solid abstractions
that enable you to create isolated, composable tasks. In the same way
that core.async gives you the tools to reason about isolated, concurrent
processes that communicate in an easily-understood way, Boot gives you
the tools to reason about tasks. In the same way that Unix processes
have the common abstraction of file handles, including standard input
and standard output, Boot provides a way to communicate between tasks.

That's a lot of high-level description, which hopefully is great for
when you want to hook someone's attention, which hopefully I have now
done. But I would be ashamed to leave you with a plateful of
metaphors. Oh no, dear reader; that was only the appetizer. For the
rest of this article you will learn what that word salad means by
building your own Boot tasks. Along the way, you'll discover that
build tools can actually have an underlying conceptual framekwork.

## Boot Tenet #1: It's Programming

In contrast to tools like [Leiningen](http://leiningen.org/) or
[Grunt](http://gruntjs.com/), Boot doesn't rely on a
data-structure-based mini-language to drive the behavior and
interactions of tasks. You end up losing the power and flexibility of
a full programming language without any gains - the configurations
aren't any easier to understand or reason about.

Instead, it sees task running as a programming problem. You can think
about your tasks the same way you think about Clojure programs,
employing the same mental models.

Tasks are meant to be stateful.

Provides abstractions that make it easy to provide interfaces between
the command line and Clojure code.

`deftask` creates a function with helpful wrappers
