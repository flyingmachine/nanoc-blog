---
title: "How Clojure Babies are Made: What Leiningen Is"
created_at: Apr 5 9:23:00 -0500 2013
kind: article
categories: programming, clojure
summary: The Clojure packaging ecosystem
draft: true
additional_stylesheets:
  - pygments
---
## What Makes Clojure Difficult

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
