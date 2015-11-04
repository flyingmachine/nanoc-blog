---
title: "Chunky Bacon in the Land of Lisp: a Rubyist Becomes Intimately Acquainted with Parentheses"
created_at: Mar 3 18:32:00 -0500 2012
kind: article
categories: programming
summary: Almost seven years since my first exposure to Ruby, I've found another language to be excited about.
---

## Ruby is Awesome

Like many Rubyists, I came to the language through Rails while fleeing
the horror of convoluted PHP sphagetti hell. For web development, the
Rails framework was a relief; for programming, Ruby was a revelation.

When asked, most other Ruby programmers I know will say they feel the
same way. Ruby was
[designed to make programmers happy](http://www.artima.com/intv/rubyP.html)
and it shows. Ruby is remarkably free of cruft. Its succinctness and
cohesion allow you to go from idea to implementation quickly.

What this all boils down to is that Ruby requires far less "mental
overhead", leaving room for creative problem solving. Our memories and
attention spans are limited, and every time we get caught up in the
weird minutiae of a programming language we're likely to forget what
we were trying to do in the first place. We then need to piece
together our original purpose and plan of attack, breaking our flow
and causing frustration. Because Ruby introduces less of a cognitive
burden, we're able to stay "in the zone" longer and experience the
pleasure of creativity.

In addition to its design, Ruby offers power and flexibility. As in
the old Perl mantra, it makes easy things easy and hard things
possible through such features as closures, meta programming, mixins,
message passing and "duck typing". All of these ideas were new to me
back in 2005 when I got my first taste of Ruby, and it was so exciting
to learn about them and use them that for months I spent almost every
morning and evening seeing what I could do with this awesome language.

## Lisp is Awesome

It's been awhile since I've felt this excited about a
language. Javascript, which I pretty much learned in tandem with Ruby,
was a lot of fun. Objective-C was frustrating because I didn't know C
and couldn't find a comprehensive guide to the mammoth framework(s?)
used for cocoa development. Learning C was a fun challenge and it's
been useful in understanding Unix, but it hasn't really sustained my
interest.

And now I've met Lisp. Common Lisp, to be exact. And I find myself
just as excited as when I started learning Ruby. I don't even really
know what I would use the language _for_ - it's just fun to learn it
and see what I can do. For the time being, I've been occupying myself
with implementing
[Tic-Tac-Toe in Lisp and Ruby](https://github.com/flyingmachine/minimax-tictactoe),
as I wrote about in my [article on minimax](/programming/minimax). (By
the way - I plan on writing an article comparing the two versions.)
Next I plan to optimize those implementations a little bit, and after
that I shall work on my magnum opus: _Hobbit vs. Giant_.

But back to Lisp. Like with Ruby, the language does not get in my
way. And just like when I came from PHP, I'm finding myself with a
wealth of new ideas and programming tools to play with. Generic
functions and classes, multi-methods, first-class treatment of
functions and closures - the list could go on. But chief among Lisp's
features is the mighty *macro*.

## Macros are Awesome

One thing that really blew my mind about Ruby is the power you get
from its meta-programming. Though I've barely scratched the surface in
Lisp, I have the sense that Lisp's macro system offers _so much more_.

It's hard for me to explain why I think macros are cool except by
analogy. So - consider that, in Ruby, everything is an object. This
simplifying concept reduces cognitive burden, but it also means that
you can extend Ruby's power to everything. You can open up the String
class and define methods on it because it's just a class like every
other.

In Lisp, code is data. What this means is that the code you write to
do stuff takes the same form as the code you write to represent a
basic Lisp data structure, the list. For example, this is how you
would add two numbers in Lisp:

```clojure
(+ 1 2)
```

Now consider how you would write a list constant:

```
'(+ 1 2)
```

The single quote indicates, "Don't evaluate me, just use me as data".

Because the code that you write in order to _do stuff_ takes the same
form as a list, Lisp is able to treat code as data and use the full
power of the language to manipulate your code.

This is what gives macros their power. By contrast, in Ruby you
perform metaprogramming largely through string manipulation and
symbols, As Peter Seibel explains in [Practical Common
Lisp](http://www.gigamonkeys.com/book/macros-defining-your-own.html):

    The key to understanding macros is to be quite clear about the
    distinction between the code that generates code (macros) and the code
    that eventually makes up the program (everything else). When you write
    macros, you're writing programs that will be used by the compiler to
    generate the code that will then be compiled. Only after all the
    macros have been fully expanded and the resulting code compiled can
    the program actually be run. The time when macros run is called macro
    expansion time; this is distinct from runtime, when regular code,
    including the code generated by macros, runs.

As I write this, I find that I'm not really doing the subject
justice. That's probably inevitable. Before really learning what
closures are and how to use them, most Rubyists probably didn't see
what the big deal was. However, I hope that this explanation might
inspire other Rubyists did give this language a try. My guess is that
most who really give it a shot will come to enjoy it as I do. The
point is that Lisp offers a great deal of power and flexibility in an
elegant package, all language aspects that Rubyists appreciate. The
territory is worth exploring.

## Lisp Resources

To get started, I recommend
[Land of Lisp: Learn to Program in Lisp, One Game at a Time!](http://amzn.to/1MyWXUg). I
would say that it rivals _why's book in fun, creativity, and
usefulness.

As a follow-up, I recommend the aforementioned
[Practical Common Lisp](http://www.gigamonkeys.com/book/). It's also a
good choice to get started immediately as it's available online for
free. (Incidentally, it's quite pleasant to read as the typography is
good.) It's similar to the Pickaxe in that it gives a much more
thorough and straightforward explanation of the language. It has
really helped me fill in the gaps. The following resources are also
useful:

* [r/lisp](http://www.reddit.com/r/lisp) - It's fun to see what's going on in the lisp world. The community has also been friendly and helpful.
* [The Common Lisp HyperSpec](http://www.lispworks.com/documentation/HyperSpec/Front/index.htm) - The master CL reference.
* [On Lisp](http://www.paulgraham.com/onlisp.html) - Paul Graham's
  classic, for free. I haven't actually started this one yet but I'll
  be going through it next.
* [Let Over Lambda](http://amzn.to/1Ww8AWV) - I've only just started this and it's probably too advanced for me, but holy crap is it exciting to read!
