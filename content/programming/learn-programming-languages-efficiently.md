---
title: Techniques for Efficiently Learning Programming Languages
created_at: Apr 16, 2017
categories: programming
summary: What follows are the best techniques for learning programming languages that I've picked up over years of teaching programming.
kind: article
---

Learning programming languages is a skill: do it well and you'll
experience one dopamine hit after another as you master something new;
do it poorly and you'll feel constantly frustrated and even give
up. What follows are the best techniques for learning programming
languages that I've picked up over years of teaching programming by
[writing books](http://www.braveclojure.com/) and
[articles](http://www.flyingmachinestudios.com/programming/the-unofficial-guide-to-rich-hickeys-brain/),
[doing talks](https://www.youtube.com/watch?v=eRq5UBx6cbA), and
running
[a training course](http://www.braveclojure.com/training/). Many of
these techniques are pulled from books explaining the latest research
in efficient learning, and you can find those books (along with other
great programming books) at
[Community Picks: Learn Programming](http://www.communitypicks.com/r/learnprogramming).

## Test Yourself Constantly to Defeat The Illusion of Competence

One of the _worst_ ways to learn is to re-read or re-watch
material. This kind of review gives you the _feeling_ that you
understand the topic covered because it seems like you're
understanding the topic effortlessly. Researchers call this _the
illusion of competence_.

A significantly better approach (and one of the best techniques you
can employ) is to _test yourself constantly_. Instead of re-reading
what a function or class or object is, ask yourself to define these
concepts or use them in a short program; force yourself to somehow
demonstrate your understanding. This process often feels
uncomfortable, but it's much more efficient at forming long term
memories. You can take this one step further and _test yourself before
you've covered the material_ by, for example, attempting exercises
before reading a chapter. Remarkably, this has also been shown aid
memory formation.

The impressive impact that testing has on learning is called _the
testing effect_, and here are some specific ways you can take
advantage of it:

* Before reading a chapter or watching a video, try guessing at what
  you're about to learn and write it down.
* Try doing a chapter's exercises _before_ reading the chapter.
* Always do exercises, even the hard ones. It's OK to give up on an
  exercise and come back to it later (or never, even), but at least
  try it. (More on this in the next section.)
* Read a short program and try to recreate it without looking at the
  original code. Or, go smaller and do this with a function.
* Immediately after learning a new concept like objects, classes,
  methods, or higher-order functions, write code that demonstrates
  that concept.
* Create diagrams that illustrate concepts, both in isolation and how
  they relate to each other.
* Blog about a concept you just learned. 
* Try explaining the concept to a non-technical friend. (I did this a
  lot when writing _Clojure for the Brave and True_; being able to
  explain an idea in layman's terms forces you to understand the idea
  deeply.)

Many of these techniques boil down to _write some code!_ With
programming it's easy to believe we're learning a lot just by reading
because programming is text-heavy and conceptual. But it's also a
skill, and like any other skill you have to perform it to get
better. Writing code is the best way to reveal your incorrect
assumptions about programming. The faster you do that, the faster you
can make corrections and improve.

If you'd like to learn more about the testing effect, check out
[_make it stick: The Science of Successful Learning_](http://www.communitypicks.com/r/learnprogramming/s/17592186047889-make-it-stick-the-science-of-successful-learning).

## Take Time to Unfocus

If you're stuck on a problem or don't understand something you just
read, try taking a walk or even a shower -- anything to enter a
relaxed, unfocused state of mind. It probably seems counterintuitive
that one of the best ways to get unstuck is to stop trying for a
little while, but it's true.

The problem is that it's easy for us to put on mental blinders when
we're focusing hard on a problem. I mean, that's pretty much what
"focus" means. But by focusing hard, we're exploring only a small
portion of the solution space. By unfocusing, our unconscious mind is
able to explore and make connections across vast swaths of our
experience.

To me it's like when you're trying to find a destination on a paper
map (remember those?). You can unconsciously become convinced that the
city you're trying to reach _should be right here!_ in the upper-left
qudrant of the map, so you look at it over and over without
success. Then you put the map down and take a deep breath and stare at
nothing for a minute, and when you look at the map again the actual
location jumps out at you immediately.

We've all had the experience of having a sudden insight in the shower;
now you have a slightly better understanding of why that happens, and
you can employ the technique on purpose. Personally, I will actually
take a shower if I'm stuck on something, and it's remarkable how well
the technique works. And how clean I am.

If you'd like to learn more about the focused and diffuse modes of
thinking, check out
[A Mind for Numbers: How to Excel at Math and Science (Even If You FLunked Algebra)](http://www.communitypicks.com/r/learnprogramming/s/17592186047884-a-mind-for-numbers-how-to-excel-at).

## Don't Waste Time Being Frustrated

Related to the last section: _don't waste time being frustrated with
code_. Frustration leads us into doing stupid things like re-compiling
a program or refreshing the browser with the hope that _this time it
will be magically different_.

Use frustration as a signal that there's a gap in your knowledge. Once
you realize you're frustrated, it can help to take a step back and
clearly identify the problem. If you've written some code that's not
working, explicitly explain to yourself or someone else the result
that you expected. Use the scientific method and develop a hypothesis
for what's causing the unexpected behavior. Then test your
hypothesis. Try again, and if a solution still eludes you, put the
problem aside and come back to it later.

I can't tell you how many times I've thrown my laptop in disgust over
a seemingly unsolvable problem, only to look at it the next day and
have an obvious solution pop into my head immediately. This happened
last week, even.

## Identify Which Programming Language Aspect You're Dealing With

Personally, I find it useful to keep in mind that when you're learning
a programming language, you're actually learning four things:

* _How to write code_: syntax, semantics, and resource management
* The language's _paradigm_: object-oriented, functional, logic, etc.
* The _artifact ecosystem_: how to build and run executables and how
  to use libraries
* _Tooling_: editors, compilers, debuggers, linters

It's easy to get these four facets mixed up, with the unfortunate
result that when you run into a problem you end up looking in
completely the wrong place.

Someone who's completely new to programming, for example, might start
out by trying to build iOS apps. They might try to get their app
running on a friend's phone, only to see some message about needing a
developer certificate or whatever. This is part of the _artifact
ecosystem_, but an inexperienced person might see this as a problem
with _how to write code_. They might look at every line they wrote to
figure out the problem, when the problem isn't with their code at all.

I find it easier to learn a language if I tackle each of these aspects
systematically, and in another blog post I'll present a general list
of questions that need answering that should help you in learning any
language.

## Identify the Purpose, External Model, and Internal Model

Whenever you’re learning to use a new tool, its useful to identify its
_purpose_, _external model_ and _internal model_.

When you understand a tool's purpose, your brain gets loaded with
helpful contextual details that make it easier for you to assimilate
new knowledge. It's like working on a puzzle: when you're able to look
at a picture of the completed puzzle, it's a lot easier to fit the
pieces together. This applies to languages themselves, and language
libraries.

A tool's external model is the interface it presents and the way it
wants you to think about problem solving. Clojure’s external model is
a Lisp that wants you to think about programming as mostly
data-centric, immutable transformations. Ansible wants you to think of
server provisioning in terms of defining the end state, rather than
defining the steps you should take to get to that state.

A tool's internal model is how it transforms the inputs to its
interface into some lower-level abstraction. Clojure transforms Lisp
into JVM bytecode. Ansible transforms task definitions into shell
commands. In an ideal world, you wouldn’t have to understand the
internal model, but in reality it’s almost always helpful to
understand a tool's internal model because it gives you a unified
perspective on what might seem like confusing or contradictory
parts. When the double-helix model of DNA was discovered, for example,
it helped scientists make sense of higher-level phenonema. My point, of
course, is that this blog post is one of the greatest scientific
achievements of all time.

Tutorials often mix up a tool's external model and internal model in a
way that’s confusing for learners; it's helpful to be aware of this so
that you can easily identify when it's causing you frustration.

## Spaced Repetition Helps You Remember

Spaced Repetition been proven to be one of the best ways to encode new
information in long-term memory. The idea is to quiz yourself at
ever-increasing time intervals to minimize memory decay using the
fewest number of repetitions. The Guardian wrote a
[great introductory article](https://www.theguardian.com/education/2016/jan/23/spaced-repetition-a-hack-to-make-your-brain-store-information).


## Sleep and Exercise

Take care of your body! It's more than just a vehicle for your
brain. If you want to be able to stay focused and learn efficiently,
getting adequate sleep and exercise beats the pants off caffeine and
energy drinks.

## More tips?

If you have any useful tips, please leave them in the comments! If
you'd like more excellent resources on learning to program, check out
the
[Community Picks: Learn Programming](http://www.communitypicks.com/r/learnprogramming),
a community-curated collection of the best books for learning
programming. It includes a wide array of subjects, including
introductory programming books, books on craftsmanship, and books on
soft skills and interviews.
