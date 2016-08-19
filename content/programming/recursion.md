---
title: Understanding Recursion
created_at: Mon Aug 8 2016 8:00:00 -0500
kind: article
categories: programming
summary: "Struggle with recursion? This blahg post might help."
additional_stylesheets:
  - pygments
---

During my [Clojure training](http://www.braveclojure.com/training/)
I've found that recursion routinely trips people up. It definitely
tripped *me* up for a long time. I've tried to develop an explanation
that's clear and intuitive, so if you're still scratching your head
about recursion, read on!

A classic recursion example is calculating _n_ factorial, which is _n_
multiplied by every natural number before _n_; 3 factorial is 6
(3 times 2 times 1), 4 factorial is 24, 5 factorial is 120.

The code snippet that follows is a typical implementation of
factorial; if you're reading this, then presumably it's confusing -
which is great! It means that I haven't written this article for
nothing.

```javascript
function factorial(n) {
  if (n == 1) {
  	return n;
  } else {
  	return n * factorial(n - 1);
  }
}
```

What makes this function recursive is that `factorial` calls
itself. That's also what makes the function tricky; the function calls
itself!?

We're used to functions calling _other_ functions to get work
done. For example, this function uppercases a string and prepends
`"Yo, "` to it:

```javascript
function yoShout(str){
  return "Yo, " + str.toUpperCase();
}
yoShout("gimme a donut");
// "Yo, GIMME A DONUT"
```

In this tiny example, `yoShout` does its work by using the
`toUpperCase` function. It's easier to understand than a recursive
function because `yoShout` treats `toUpperCase` as a black-box
abstraction. You don't have to tax your brain by loading
`toUpperCase`'s implementation details into your short-term memory.

Let's re-write `factorial` to use function calls this way, with
function's body calling another function in order to get its work
done. To calculate 3 factorial, you could write a series of
functions, `factorial_1`, `factorial_2`, and `factorial_3`, like this:

```javascript
function factorial_1() {
  return 1;
}

function factorial_2() {
  return 2 * factorial_1();
}

function factorial_3() {
  return 3 * factorial_2();
}
```

These functions feel safe and comfy. `factorial_3` calls
`factorial_2`, something we're completely familiar with, and likewise
`factorial_2` calls `factorial_1`. `factorial_3` also does not care
how `factorial_2`, just like in the string example.

Unfortunately, these functions are also completely impractical; can
you imagine writing `factorial_1000`? The recursive implementation
doesn't have this problem.

My suggestion is to try seeing the recursive implementation from the
same perspective as the nonrecursive imiplementation. Here's the code
again:

```javascript
function factorial(n) {
  if (n == 1) {
  	return n;
  } else {
  	return n * factorial(n - 1);
  }
}
```

You can look at this and say, "Oh, if `n` isn't 1, then this function
does its work by calling some black-box function named `factorial`
with the argument `n - 1`." You can look at the call to `factorial(n -
1)` as a call to a completely different function - one that just
happens to have the same name and algorithm.

That's it! I hope it helps. If you've been confused by recursion, I'd
love to hear your feedback!
