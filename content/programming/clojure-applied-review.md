---
title: Review of Clojure Applied
created_at: Sun Oct 18 2015 08:00:00 -0500
kind: article
categories: programming
summary: "<em>Clojure Applied</em> is a good choice for those looking to become intermediate Clojurists. It really shines in its coverage of testing and of decomposing your system into components. Besides that, it's filled with little gems from Java and the Clojure standard library."
---

A while back, Clojure community czar and all around good guy
[Alex Miller](http://twitter.com/puredanger) asked me to review a beta
version of his nearly complete book
[*Clojure Applied*](http://amzn.to/1PqXyOi), co-authored with Ben
Vandgrift. This was great news for me because I was planning on buying
it, but this way I would get it for free. Delighted, I downloaded my
free copy, added a "review book" item to my todo list, then merrily
ignored it until a couple weeks ago when I saw Alex in person.

"Sorry I haven't gotten around to reviewing your book. I'm super
excited about it!" I gushed at him guiltily. "What's the final release
date?" Very politely, he responded, "It's already released." It turns
out, the resulting dose of healthy embarrassment was all I needed to
actually get off my ass and read and review the book!

And I am glad I did, because it's a good one. *Clojure Applied* is
aimed at people who know Clojure basics and want to learn how to write
idiomatic code and create production applications, and if this
describes you, then you should [get it](http://amzn.to/1PqXyOi). In
case you need more convincing, the rest of this post goes on about how
good the book is.

What I like most about *Clojure Applied* is that it's concise and
fast-paced. It doesn't hold your hand, blathering on about basic
details that you learned months ago. At the same time, it's very
approachable, using straightforward and brisk language. It even has a
few fun moments (two words: spaghetti tacos). To use a cliche that
offends my vegetarian sensibilities, it's all meat and no filler,
which is perfect for programmers who are... uh... hungry for
knowledge.

The most valuable parts of the book are the sections on building a
system and testing. Decomposing a your program into a system of
interacting components that manage their own state isn't a topic
covered by beginner books, and it's very helpful. It's easy to write
programs whose architectures go against Clojure's grain of functional
programming, immutability, and state management, but *Clojure Applied*
shows you how to do it right. On the testing front, *Clojure Applied*
is nice because it covers some of the latest additions to the testing
ecosystem, like `test.check`.

Another nice aspect of the book is that it introduces you to a
cornucopia of useful standard library and Java functions and
constructs, like the `juxt` function, the `reduced` function,
persistent queues, thread pools with the executor service, etc etc
etc.

All in all, it's a good book. If you're looking for a good second book
on Clojure, then *Clojure Applied* is an excellent choice!
