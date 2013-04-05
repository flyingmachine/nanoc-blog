---
title: "How Clojure Babies are Made: What Leiningen Is"
created_at: Apr 5 9:23:00 -0500 2013
kind: article
categories: programming, clojure
summary: The Clojure packaging ecosystem
additional_stylesheets:
  - pygments
---

Leiningen is an integral part of the Clojure ecosystem, in case you
haven't noticed (and if you haven't noticed &ndash; pay better
attention!). Up until now we've described specific capabilities that
Leiningen has and how those capabilities are implemented. It can
[build and run your app](/programming/how-clojure-babies-are-made-lein-run),
it [has a trampoline](/programming/lein-run), and so forth.

In this article, we'll take a step back and get a high-level
understanding of what Leiningen is. We'll do this by giving an
overview of the non-coding related tasks you need to accomplish when
building software. Next, we'll describe how Leiningen helps you
accomplish these tasks and compare it to similar technology, Ruby.

This post isn't as nitty-gritty as the previous posts in the Clojure
Babies series, but it will help lay the groundwork for the upcoming
post on packaging. Additionally, it's
