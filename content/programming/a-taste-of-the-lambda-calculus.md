---
title: A Taste of the λ Calculus
created_at: Sat Jul 20 2013 19:23:00 -0500
kind: article
categories: programming
summary: "I've been having a brain-bending good time reading An Introduction to Functional Programming Through Lambda Calculus. Using examples from that book, this article will walk you through the basics of λ calculus. We'll then look at the surprising, counterintuitive way that the λ calculus lets us represent conditional expressions and boolean operations &mdash; all with functions as the only values. It was a very different way of thinking for me, and exciting to learn. I hope it's exciting for you, too!"
---

I've been having a brain-bending good time reading
[An Introduction to Functional Programming Through Lambda Calculus](http://www.amazon.com/gp/product/B00CWR4USM/ref=as_li_ss_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B00CWR4USM&linkCode=as2&tag=aflyingmachin-20).
Using examples from that book, this article will walk you through the
basics of λ calculus. We'll then look at the surprising,
counterintuitive way that the λ calculus lets us represent conditional
expressions and boolean operations &mdash; all with functions as the only
values. It was a very different way of thinking for me, and exciting
to learn. I hope it's exciting for you, too!


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
[John McCarthy](http://en.wikipedia.org/wiki/John_McCarthy_(computer_scientist\))
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
;; Note that functions always have one and only one parameter
λx.x
λy.y ;; equivalent to above; we'll get into that more
λfirst.λsecond.first ;; the body of a function can itself be a function
λfn.λarg.(fn arg)

;; Application
(λx.x λx.x)
((λfirst.λsecond.first x) y)
```

There are two super-cool things about this specification. First, it
really boils down to four elements: names, functions, application, and
"expressions" which can be any of the above. That's awesome! Second,
function bodies and function application arguments can be any
expression at all, meaning that a) functions can take functions as
arguments and b) functions can return functions.

You can see how this is directly related to functional programming,
where you have first class functions and higher order functions.
This is interesting in itself as it gives you a glimpse of the
theoretical underpinnings of functional programming.

But it gets way, way cooler. By the end of this article you'll see how
conditions and boolean operations can be represented in terms of
functions and functions that operate on functions. In order to get
there, let's first look at how function application works. Then we'll
go over some basic but crucial functions.

## Function Application

When you *apply* a function to an *argument expression*, you *replace*
all instances of *name* within the function's *body* with the
*argument expression*.

Keep in mind that we're talking about a mathematical system here,
*not* a programming language. This is pure symbol manipulation,
without any regard for how actual hardware will carry out the
*replace* operation mentioned above.

Let's start to flesh out this purely abstract notion of function
application with some examples, starting with the identity function:

```
;; Identity function
λx.x
```

As you would expect, applying this function to an argument expression
returns the argument expression. In the example below, don't worry about
where "foo" comes from:

```
;; Apply the identity function to foo
(λx.x foo)

;; After replacing all instances of x within the body, you get:
foo
```

Makes sense, right? I'm sure that you can intuitively understand
what's going on in function application. Nevertheless, I think we can
make it clearer by looking at a few examples:

```
(λs.(s s) foo) =>
(foo foo)

(λx.λy.x foo)
λy.foo

(λa.λb.λc.((a b) c) foo)
λb.λc.((foo b) c)
```

For a more thorough explanation of what's going on here, please see
[Jon Sterling's comment](http://www.flyingmachinestudios.com/programming/a-taste-of-the-lambda-calculus/#comment-971908704) below!

Now that we understand how to apply functions, let's explore a few
more basic functions.

## The Self-Application Function

The self-application function evaluates to the application of its
argument to itself:

```
λs.(s s)
```

Let's see an example:

```
;; Apply the self-application function to the identity function
(λs.(s s) λx.x)

;; Perform replacement - results in an application
(λx.x λx.x)

;; Perform another replacement
λx.x
```

Now let's make things interesting:

```
;; Apply the self-application function to itself
(λs.(s s) λs.(s s))

;; Perform replacement
(λs.(s s) λs.(s s))

;; Hmmm this is exactly like the first expression. Let's perform
;; replacement again just for kicks
(λs.(s s) λs.(s s))
```

How about that, it turns out that it's possible for evaluation to
never terminate. Fun!

## The Function Application Function

Check this out:

```
λfunc.λarg.(func arg)
```

This function takes a function as its argument, returning a function:

```
(λfunc.λarg.(func arg) λx.x) =>
λarg.(λx.x arg)
```

When you apply this resulting function to an argument, the end result
is that the function you supplied as the first argument gets applied
to the current argument:

```
;; Notice the identity function nestled next to the 
;; second left parenthesis
(λarg.(λx.x arg) λs.(s s))
```

Here's the whole application:

```
((λfunc.λarg.(func arg) λx.x) λs.(s s)) =>
(λarg.(λx.x arg) λs.(s s)) =>
(λx.x λs.(s s))
λs.(s s)
```

Is your head hurting yet? I sure hope so! That's your brain's way of
letting you know that it's learning!

We're starting to get a hint of the cool things you can do with λ
calculus. It only gets cooler from here!

## Interlude: Give the Functions Names, Already!

Before this post gets overwhelmed with "λx.x" and "λs.(s s)" and such,
let's introduce some syntax:

```
;; Name functions
def <name> = <function>

;; Examples
def identity = λx.x
def self_apply = λs.(s s)
def apply = λfunc.λarg.(func arg)
```

Now wherever we see `<name>`, we can substitute `<function>`.
Examples:

```
(identity identity) =>
(λx.x identity) =>
identity

(self_apply identity) =>
(λs.(s s) identity) =>
(identity identity) =>
identity

((apply idenity) self_apply) =>
((λfunc.λarg.(func arg) identity) self_apply) =>
(λarg.(identity arg) self_apply) =>
(identity self_apply) =>
self_apply
```

Make sense? Excellent! This will let us break your brain with greater
efficiency. Now pay attention, because things are about to get super
flippin' fantastic.

## Argument Selection and Argument Pairing Functions

In the λ calculus, functions by definition have one and only one
parameter, the *name*. This might seem limiting, but it turns out that
you can build functions which allow you to work on multiple arguments.

The following functions together allow you to select either the first
or the second of two arguments. We'll look at them all together first
and then dig in to see how they work together.

```
def make_pair     = λfirst.λsecond.λfunc.((func first) second)
def select_first  = λfirst.λsecond.first
def select_second = λfirst.λsecond.second
```

`select_first` and `select_second` do what their names suggest,
selecting either the first or second of two arguments. They have the
same underlying structure; they're both functions which take a `first`
argument and evaluate to a function. This function is applied to a
`second` argument. `select_first` returns `first`, and `select_second`
returns `second`.

Let's see how this works with `select_first`:

```
;; Start here
((select_first identity) apply)

;; Substitute the function itself for "select_first"
((λfirst.λsecond.first identity) apply)

;; Perform the first function application, replacing "first" with "identity".
;; This returns another function, which we'll apply to a second argument.
;; Notice that the body of the resulting function is "identity", and
;; the name "second" doesn't appear in the body at all
(λsecond.identity apply)

;; Apply function. Since "second" doesn't appear in the function body,
;; it disappears into the ether.
identity
```

`select_second` uses the same principle:

```
((select_second identity) apply)
((λfirst.λsecond.second identity) apply)
(λsecond.second apply)
apply
```

So, `select_first` and `select_second` are able to operate on a pair
of arguments.

But how do we create pairs for the to work on? `make_pair` creates a
"pair" by returning a function which expects either `select_first` or
`select_second` as its argument. This is awesome - we don't need any
data structures to represent a pair, all we need are functions!

Let's actually create a pair:

```
;; Start here
((make_pair identity) apply)

;; Substitute the actual function
((λfirst.λsecond.λfunc.((func first) second) identity) apply)

;; Perform first function application, replacing "first" with "identity"
(λsecond.λfunc.((func identity) second) apply)

;; Perform remaining function application, replacing "second" with "apply"
λfunc.((func identity) apply)
```

This resulting function looks very familiar! Let's compare it with our
`select_first` and `select_second` applications above:

```
;; Result of ((make_pair identity) apply)
λfunc.((func identity) apply)

;; Application of select_first and select_second
((select_first identity) apply)
((select_second identity) apply)

;; Apply the result of make_pair to select_first
(λfunc.((func identity) apply) select_first) =>
((select_first identity) apply)
```

So, to reiterate, `make_pair` works by taking a `first` argument. This
returns a function with takes a `second` argument. The result is a
function which you can apply to either `select_first` or
`select_second` to get the argument you want.

This is super freaking cool! A pair is a function which has "captured"
two arguments and which you then apply to a selection function.
Starting with just four basic constructs &ndash; names, functions,
applications, expressions &ndash; and five simple rules for performing
function application, we've been able to construct pairs of arguments
and select between them.

And things are about to get even more fun! We're now ready to see how
we can create conditional expressions and boolean operations purely
using λ expressions.

## Conditional Expressions and Boolean Operations

The upcoming treatment of conditional expressions and boolean
operations is going to look kinda weird at first. You'll want to keep
in mind that in abstract math, elements don't have any inherent
meaning but are defined by the way with they interact with each other
&mdash; by their behavior.

For our purposes, the behavior of a conditional expression is to
select between one of two expressions, as shown by the following
pseudocode:

```
if true
  <expression>
else
  <expression>
end
```

Hmm... selecting between two expressions... we just went over that!
`make_pair` gave us a pair of expressions to choose between using
either `select_first` or `select_second`.

Because these functions result in the exact same behavior as if/else,
let's gon ahead repurpose these:

```
;; This is identical to make_pair
def cond = λe1.λe2.λc((c e1) e2)
def true = select_first
def false = select_second

;; Apply a conditional expression to true, aka select_first
(((cond <e1>) <e2>) true) =>
<e1>

;; Apply a conditional expression to false aka select_second
(((cond <e1>) <e2>) false) =>
<e2>
```

You're probably not used to thinking of a conditional expression as a
function which you apply to either true or false, but it works!

## NOT, AND, OR

NOT can be seen as

```
if x
  false
else
  true
end
```

So, if `x` is `true` then `false` is selected, and if `x` is `false`
then `true` is selected. Let's look at this using the `cond`
expressions above:

```
;; In general
(((cond <e1>) <e2>) true) =>
<e1>
(((cond <e1>) <e2>) false) =>
<e2>

;; For NOT
(((cond false) true) true) =>
false
(((cond false) true) false) =>
false

;; So in general, we can say:
def not = λbool.(((cond false) true) bool)

;; We can simplify this, though I'm lazy and won't show how:
def not = λbool.((bool false) true)
```

AND can be seen as

```
if x
  y
else
  false
end
```

In other words, if `x` is true then the value of the expression is the
value of `y`, otherwise it's false. Here's how we can represent that:

```
def and = λx.λy.((x y) false)
```

Keep in mind that `true` is `select_first` and `false` is
`select_second`:

```
;; select_first is true
;; when x is true, the value of the entire expression is the value of y
(λx.λy.((x y) false) select_first) =>
λy.((select_first y) false)

;; select_second is false
;; when x is false, the second argument, false, is selected
(λx.λy.((x y) false) select_second) =>
λy.((select_second y) false)
```

We can treat OR similarly:

```
if x
  true
else
  y
end
```

We can capture this with

```
def or = λx.λy.((x true) y)
```

I won't work this one out - I'll leave it "as an exercise for the
reader." :)

## The End

I hope you've enjoyed this brief taste of the λ calculus! We've only
scratched the surface of the kinds of neat things it's capable of. If
you thought this article was fun, then I definitely recommend
[An Introduction to Functional Programming Through Lambda Calculus](http://www.amazon.com/gp/product/B00CWR4USM/ref=as_li_ss_tl?ie=UTF8&camp=1789&creative=390957&creativeASIN=B00CWR4USM&linkCode=as2&tag=aflyingmachin-20).
This fun tome provided most or all of the examples I've used, though
I've tried to present them in a way that's easier to understand. I
also recommend
[The Art of Lisp & Writing](http://www.dreamsongs.com/ArtOfLisp.html),
which conveys the beauty and joy of coding in Lisp.
