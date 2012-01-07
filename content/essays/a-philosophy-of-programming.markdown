---
title: A Philosophy of Programming, Rough Sketch
created_at: Thu Jan 5 14:02:00 -0500 2012
kind: article
summary: An attempt to describe an approach to programming within an integrated framework.
---

# A Philosophy of Programming, Rough Sketch

Below is a first attempt at describing an integrated approach to
programming. My hope is that it'll give programmers some tools to
reason about their current practices and generate ideas on new
practices to implement.

The unifying idea is that our work is governed by cognitive processes
and that we can better understand our work by understanding those
processes. There is a wealth of information available on such topics
as memory, attention, focus, and emotion, and I believe that it will
be fruitful to relate such information directly to the processes
involved in programming.

Put another way, understanding our hardware will help us understand
the behavior of our software.

There are a lot of gaps. My hope is that I'll get a lot of feedback
that will help me fill in those gaps.

## The Processes

There are three main processes involved in programming:

1. Learning
2. Problem Solving
3. Social interaction

This article only addresses learning. Future articles will address
the other two topics.

## Learning

We constantly need to learn: existing codebases, new libraries, the problem
domain, the application environment.

Learning consists of relating new information to existing knowledge.

## Memory

Ease of learning is in part determined by short term memory. To learn,
it is necessary to hold multiple mental objects in short term memory in
order to compare them to each other and discover the relationships
between them.

Not all mental objects are created equal. Some take up more "space"
than others. Familiar objects take up less space than unfamiliar
objects. For example, the following list is relatively easy to hold in
memory:

* bed
* cup
* light

The following list is harder to hold in memory:

* clorp
* farg
* dwesh

Short term memory also has a temporal limitation (obviously). We
should avoid creating the need to hold objects in memory for longer
than our brains are designed for. We've all experienced the strain
produced by having to navigate back and forth among multiple files in
order to figure something out. More on this later.

### Naming

The nature of memory explains why it is necessary to name things well
in code. The explanation is two-fold (using method names as an
example):

* Without proper naming, it is more likely to be necessary to derive
  the behavior of a method from its constituent parts. This requires
  loading more items into your short-term memory, possibly resulting
  in your forgetting why you were looking at the method in the first
  place. The result is that learning time is increased.
* Indescript names also take up more "space" in short term memory,
  making it more difficult to understand how a method relates to the
  larger system.

Examples needed.

### Metaphor

Metaphor is at the heart of programming: stacks, queues, pipes,
memory, objects, domain models. These are all metaphors. 
Metaphor is at the heart of our craft.

We also use the term "abstraction", but I think "metaphor" is better
when it comes to naming, as it more directly denotes that we are
relating new concepts to existing concepts. But the distinction really
doesn't matter.

Good metaphors allow us to hold more information-dense items in
memory. They provide more "hooks" which allow for easier retrieval
from long-term memory.

Other "metaphors":

* Visual form of code. Indentiation stands for hierarchy.
* Code colorization. Color stands for syntax.
* Programming style. Shared programming style allows people to derive
  meaning from the structure of the code.

All of these examples of metaphor reduce the number of mental objects
we need to retain in short-term memory, thus making learning easier.
You can easily confirm that it's much easier to hold "stack" in memory
than it is to hold "a last in, first out abstract data type and linear
data structure."

### Visual Aids

Visual aids are information-dense. Our visual brains are pretty
awesome at processing visual aids. My little treatise on visual
design: [Clean Up Your Mess](http://www.visualmess.com).

### Documentation

API documentation is not enough. People who use your code have the
following questions, among others:

* What is the purpose of the system?
* What are the subsystems, and what are their purposes?
* How do the parts interact?
* How do I extend it?

It's not reasonable to expect someone to answer these questions by
reading code and API documentation. Because there are so many pieces
involved, it is very difficult for someone to a) determine which
pieces are relevant and b) hold the relevant pieces in mind long
enough to deduce the answer to his question. Such a process often
involves navigating multiple files, constantly having to refresh our
memories by looking at the same pieces of code over and over.

It's possible to answer questions like the ones listed above using the
process I listed above, but it's not optimal. Good documentation
condenses the information, making it understandable without producing
so much cognitive load.

Examples of good documentation:

* [nanoc](http://nanoc.stoneship.org/docs/). It was very easy to learn
  how to use nanoc because the documentation is excellent. If I had to
  learn how to use it by reading the code, I wouldn't have bothered.
  If I had to learn solely by going through examples, it would still
  have been very difficult. Instead, the documentation provides a
  mixture of prose and examples which made learning the system very
  easy.
* [Whoops](http://www.whoopsapp.com/). Well, maybe I'm being a little
  vain and/or delusional here, but I think my documentation for Whoops
  is above-average. There's still a good amount that I could cover,
  though.

Also see [my article on
documentation](/programming/writing-better-documentation/)

## Learning Skills

### Describe What You Don't Know

Being able to concretely determine what it is you need to know is of
great value. Learn to quickly transform the emotion of confusion into
a concrete statement of what you need to learn.

### Ask About it Directly

Often, when seeking help, we ask the wrong question. Need examples.

### Confirm Your Knowledge

State and restate what you think you know to someone else who can tell
you whether you're wrong. Often, we make a lot of unconscious
assumptions when trying to learn, and this process will help you
uncover those assumptions.

## To explore

* How does emotional state affect learning?
* How is emotion related to certrainty?
* What is the relationship between testing, memory, and learning?
* How can teams support learning?
* Find concrete examples of codebases which support learning
* How information navigation is related to learning, e.g. including a 
  table of contents in documentation

## Further Reading

* [Pragmatic Thinking and
  Learning](http://www.amazon.com/gp/product/1934356050/ref=as_li_ss_tl?ie=UTF8&tag=aflyingmachin-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=1934356050)
  . Learn how to learn.
* [Your Brain at Work: Strategies for Overcoming Distraction,
  Regaining Focus, and Working Smarter All Day
  Long](http://www.amazon.com/gp/product/0061771295/ref=as_li_ss_tl?ie=UTF8&tag=aflyingmachin-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=0061771295)
  . This is one of the most useful books I've ever read. It gave me a
  useful understanding of how my brain works, and how to make use of
  that knowledge to improve my work life.
