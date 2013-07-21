---
title: A Taste of the λ Calculus
created_at: Sat Jul 20 2013 19:23:00 -0500
kind: article
categories: programming
summary: "Having put together a website using Noir, I wanted to to try and get closer to the metal. Here are some of my findings, including: templating heaven with Enlive and Middleman; using macros to enforce full stack consistency; roll-your-own-validations; more!"
---

I've been having a brain-bending good time reading
[An Introduction to Functional Programming Through Lambda Calculus](http://www.amazon.com/gp/product/B00CWR4USM/ref=as_li_ss_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B00CWR4USM&linkCode=as2&tag=aflyingmachin-20).
Below, you'll find surprising examples of how you can represent
conditions, boolean operations, and integers in λ calculus. I hope
they tickle your mathematical fancy!

## A Bit of History

As every aspiring greybeard knows, the λ calculus was invented by
[Alonzo Church](http://en.wikipedia.org/wiki/Alonzo_Church) in
response to David Hilbert's 1928
[Entscheidungsproblem](http://en.wikipedia.org/wiki/Entscheidungsproblem).
The Entscheidungsproblem inspired another computational model which
you may have heard of, the
[Turing Machine](http://www.decodedscience.com/the-turing-machine-versus-the-decision-problem-of-hilbert/14072).

The λ calculus is one of the foundations of computer science. It's
perhaps most famous for serving as the basis of Lisp, invented (or
discovered, if you prefer to think of Lisp as being on par with the
theory of gravity or the theory of evolution) by
[John McCarthy](http://en.wikipedia.org/wiki/John_McCarthy_(computer_scientist))
in 1958.

Indeed, by examining the λ calculus, you can see where Lisp derives
its beauty. The λ calculus had a lean syntax and dead-simple
semantics, the very definition of mathematical elegance, yet it's
capable of representing all computable functions.

## Enough history! Tell Me About λ Expressions!

The λ calculus is all about manipulating λ expressions. Below is its
specification. If you don't know what something means, don't worry
about it at this point - this is just an overview and we'll dig into
it more.

```
 <expression> ::= <name>
                | <function>
                | <application>
       <name> ::= any sequence of non-blank characters
   <function> ::= λ<name>.<body>
       <body> ::= <expression>
<application> ::= (<function expression> <argument expression>)
<function expression> ::= <expression>
<argument expression> ::= <expression>

;; Examples

;; Names
x
joey
queen-amidala

;; Functions
λx.x
λy.y ;; equivalent to above; we'll get into that more
λfirst.λsecond.first ;; the body of a function can itself be a function
λfn.λarg.(fn arg)

;; Applications
(λx.x λx.x)
((λfirst.λsecond.first x) y)
```

