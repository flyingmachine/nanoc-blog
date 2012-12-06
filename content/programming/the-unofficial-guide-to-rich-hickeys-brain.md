---
title: The Unofficial Guide to Rich Hickey's Brain
created_at: 6 Dec 2012
kind: article
categories: programming
summary: Part of the excitement of working with Clojure is being exposed to Rich Hickey's thoughts on programming. Rich Hickey has a clear, consistent way of viewing fundamental programming concepts that I think any programmer would benefit from. Here is the beginning of my attempt to catalog Mr. Hickey's unique viewpoint.
draft: true
---

# The Unofficial Guide to Rich Hickey's Brain

Part of the excitement of working with Clojure is being exposed to
Rich Hickey's thoughts on programming. Rich Hickey has a clear,
consistent way of viewing fundamental programming concepts that I
think any programmer would benefit from. Every time I watch one of his
talks, I feel like someone has gone in and organized the cluttered,
messy garage of my brain.

In this article (and more to come), I begin my attempt to catalog Mr.
Hickey's unique viewpoint. Eventually, I would like to produce a
concise summary of his ideas. My hope is that this will provide an
easily-scannable reference to Clojurists and an accessible
introduction to non-Clojurists.

What follows is derived from Rich Hickey's talk,
"[Are we there yet?](http://www.infoq.com/presentations/Are-We-There-Yet-Rich-Hickey)"

## Introduction

Today's OOP languages - Ruby, Java, Python, etc. - are fundamentally
flawed. They introduce
[accidental complexity](http://en.wikipedia.org/wiki/No_Silver_Bullet)
by building on an inaccurate model of reality. Where they have
explicit definitions for the following concepts, the definitions are
wrong:

* Time
* Value
* Identity
* State
* Behavior

Below, we'll contrast the OOP viewpoint on each of these topics with
the Functional Programming viewpoint. But first, we'll start by
comparing their models of reality.

## Metaphysics, Programming, and You: Comparing OOP and FP

