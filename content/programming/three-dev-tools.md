---
title: Three Dev Tools You Probably Already Know
created_at: Sun Aug 26 18:55:00 -0500 2012
kind: article
categories: programming
summary: "You probably already know about the following tools, but I found it useful to be reminded of them."
---

You probably already know about the following tools, but I found it
useful to be reminded of them:repl

## Partitioning

Partitioning is effective as a strategy to combat complexity and scale when two conditions are true: first, the divided parts must be sufficiently small that a person can now solve them; second, it must be possible to reason about how the parts assemble into a whole. Parts that are encapsulated are easier to reason about, because you need to track fewer details when composing the parts into a solution. You can forget, at least temporarily, about the details inside the other parts. This allows the developer to more easily reason about how the parts will interact with each other.

## Knowledge

Software developers use knowledge of prior problems to help them solve current ones. This knowledge can be implicit know-how or explicitly written down. It can be specific, as in which components work well with others, or general, as in techniques for optimizing a database table layout. It comes in many forms, including books, lectures, pattern descriptions, source code, design documents, or sketches on a whiteboard.

## Abstraction

Abstraction can effectively combat complexity and scale because it shrinks problems, and smaller problems are easier to reason about. If you are driving from New York to Los Angeles, you can simplify the navigation problem by considering only highways. By hiding details (excluding the option of driving across fields or parking lots), you have shrunken the number of options to consider, making the problem easier to reason about.

From _Just Enough Software Architecture: A Risk-Driven Approach_ by George H. Fairbanks
