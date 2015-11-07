---
title: Why Clojure's a Great Next Language for Rubyists
created_at: Sat Nov 7 2015 11:00:00 -0400
kind: article
categories: programming
summary: "Rubyists enjoy Ruby because it's simple, powerful, and a joy to use. Clojure has all of those qualities, plus it employs a completely different paradigm that's crazy fun to learn. If you're looking to learn a new language, Clojure's a great choice."
---

> HAVE
> *frothing at the mouth*
> YOU
> *flailing arms*
> CONSIDERED
> *eyes are unfocused and mad*
> LISP
> *flipping furniture over*
>
> &mdash; [@ra](https://twitter.com/ra/status/661707687386853376)

If you're a Rubyist itching to learn a new language, then I want to
convince you (using only a minimum of mouth frothing and chair
flipping) that Clojure's a great pick for you. Please, please excuse
me if this article reeks of the glassy-eyed fanaticism of the true
believer, but the fact is that I think Clojure is crazy stupid fun to
use, and intellectually rewarding to boot.

Ever since I fell in love with Ruby back in 2005, only Clojure has
been able to elicit the same level of affection. I think this is
because Clojure has the same fundamental attributes that make Ruby so
appealing: it's *simple*, it's *powerful*, and above all, it's
*fun*. The rest of this love letter *cough* I mean, blog article, is
about how Ruby and Clojure exhibit these attributes in very different
ways, and how Clojure's different approach to computation makes it
fascinating and rewarding to learn.

My first exposure to Ruby was
[DHH's whoops-laden Ruby on Rails demo](https://www.youtube.com/watch?v=Gzj723LkRJY).
That video saved me from the Lovecraftian horror that was PHP
circa 2005. You may have had a similar experience, with Ruby rescuing
you from C++ or Java or COBOL or whatever. Ruby

It's a vast understatement to say that, compared to PHP, Ruby is
elegant. Whereas PHP seemed almost to strive for inconsistency, Ruby
employs only a few, simple ideas: everything is an object, you advance
your program by sending messagse to objects, and message dispatch is
governed by a couple easily-understood rules.

Clojure is also a very simple language. It wouldn't be a stretch to
say that simplicity is one of the core tenets of the Clojure
philosophy; its creator, Rich Hickey, has given a number of talks
elaborating on what it means for something to be simple, including
[Simple Made Easy](http://www.infoq.com/presentations/Simple-Made-Easy)
and
[Clojure, Made Simple](https://www.youtube.com/watch?v=VSdnJDO-xdg).

Clojure's simplicity, however, takes a radically different
form. In Clojure, you primarily care about two things: data and
functions. You pass data to functions and get new data back. Yes,
there's other interesting stuff like state management constructs and
macros, just as there are other interesting aspects of ruby like
modules and blocks. But the heart of Clojure is so simple any eighth
grade algebra student can understand: hand some value to a function,
and it hands you a new value back.

"But wait a minute," you might be thinking, "You can't just pass data
around like that. Haven't you read POODR? What about encapsulation?
What if one of those functions turns your data into something crazy,
like a, uh, condescending walrus with goatee?"

In Clojure, data is *immutable*. You can't change existing data, you
can only derive new data; when you add 3 and 4, you don't change 3 or
4 to make 7, you derive 7. If this sounds crazypants and impossible to
work with, all I can do is assure you that it does work (and point you
to this book I wrote available free online,
[Clojure for the Brave and True](http://www.braveclojure.com/) which
explains how) and hope that your intellectual curiosity will motivate
you to explore this crazypants paradigm that lots of people seem to
love. Here's a teaser: many of the problems addressed by
object-oriented design simply aren't problems in Clojure. Learning
Clojure will result in a surplus of unused brain cycles.

As a Rubyist, you know that simplicity buys you power, where power is
defined as the ability to express computational ideas
concisely. For example, Ruby's blocks allow you to write anonymous
algorithms, something that's impossible in C.

Another example: Ruby's simple method dispatch scheme makes it easy
for you to reason about and design robust programs. It lets you easily
extend code and buys you reuse through multiple inheritance. Most
enticingly, it lets you do metaprogramming.

Clojure's simplicity also buys you power. As a Lisp, Clojure employs a
unify syntax which just so happens to mirror the internal
abstract syntax trees that represent Clojure code. I'm going to wave
my hands a bit here and say that this lets you use *macros*, the most
powerful metaprogramming tool available to any language. In the Ruby
community, we like to talk about code that writes code; macros take
this idea to a whole different level. It's not an exaggeration to say
that Ruby's metaprogramming system provides only a subset of the
capability provided by Lisp macros.

Super duper hand wavy, I know, but the point is: if you like how
powerful Ruby is, you will lose your flipping mind over how powerful
Clojure is. Macros let you easily implement ideas from all corners of
computer science. It's not an accident that Clojure has so many
libraries that let you use different paradigms. There's *core.logic*
for logic programming, *instaparse* for building parser generators,
*core.typed* for haskell-ish typed programming, and much more.

And macros aren't even the coolest part. I'll let you in on a secret:
macros are the flashy, shiny bait that wild-eyed Clojurists lay out to
ensnare curious programmers. Nay, even more powerful is Clojure's
design of *programming to abstractions*. This design makes it
incredibly easy to reuse your code and extend existing code. Of course
I can't tell you *how* in the short space afforded by this digital
outburst, but you'll grow as a programmer if you learn about it.

These last two paragraphs also hint at what makes Clojure so dadgum
fun. First, by learning Clojure you get introduced to one of the most
loved and respected computational traditions, Lisp and the lambda
calculus. It also has a clear philosophy, articulated by Rich Hickey,
that will make you a better programmer. Not only that, other
programming vistas become more readily accessible.

Second, Clojure is a joy to write. Here's a quote from Matz (Ruby's
creator) on his design philosophy:

> By using Ruby, I want to concentrate the things I do, not the magical rules of the language, like starting with public void something something something to say, "print hello world." I just want to say, "print this!" I don't want all the surrounding magic keywords. I just want to concentrate on the task. That's the basic idea. So I have tried to make Ruby code concise and succinct.

Clojure, likewise, is concise. It lets you focus on solving the
problem at hand rather than figuring out the magic whatever. People
are falling in love with Clojure, spending their nights and weekends
learning it hacking with it, because the experience is its own reward.

If you're looking to stretch yourself and explore more of what the
world of programming has to offer, then I recommend Clojure to you
with all of my crazed heart. Here are some resources for getting
started:

* [Clojure Distilled](http://yogthos.github.io/ClojureDistilled.html),
  a very short overview of the language
* [Clojure for the Brave and True](http://braveclojure.com), my own
  full-length crazypants introductory book
* [Living Clojure](http://amzn.to/1WIxT2Q), an excellent introductory
  book by Carin Meier, someone who BUILDS ROBOTS
* [LispCasts](http://www.purelyfunctional.tv/) for those of you who
  prefer video

Now go out there and start learning some Clojure!
