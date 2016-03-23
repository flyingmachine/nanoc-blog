---
title: Timeless Programming Tools
created_at: Wed Mar 23 2016 08:00:00 -0500
kind: article
categories: programming
summary: I've been programming professionally for a dozen years. Some of what I've learned is best forgotten, but there have been many tools and techniques that continue to be useful every day.
---

I've been programming professionally for a dozen years. Some of what
I've learned is best forgotten (oh god, Cold Fusion), but there have
been many tools, techniques, and concepts that continue to be useful
every day. Here are mine; I'd love to hear what yours are so I can
experience both the joy of discovery and regret for not learning about
the tool earlier.

### Regular Expressions

Yeah, yeah, we've all heard the joke: "something something regular
expressions, then you have two problems." Personally, I don't get it,
because regular expressions are _seriously badass_. I remember going
through O'Reilly's big fat regex book while I worked from 11pm till
7am as a night auditor at a hotel when I was 18, and being blown away
at how powerful they are. To say that we programmers deal with text
all the time is so obvious, it's not even worth saying. Regular
expressions are an essential tool,
[and here's where you can learn to use them](http://regexone.com/)

### Finite State Machines

Regular expressions are built as _finite state machines_. Here's
[a great tutorial on FSMs](http://www.gamedev.net/page/resources/_/technical/general-programming/finite-state-machines-and-regular-expressions-r3176)
showing how to actually build a regular expression. It's extremely
cool!

I think FSMs are covered in computer science 101, but since I only
went to college for a year and even then I studied works written a
couple millennia before before the computer revolution, I didn't
actually learn about them until about six years ago. My colleagues and
I were having trouble with a mobile app - we needed the initialization
process to happen in a particular way, and the logic for ensuring that
was getting pretty tangled.

Once we took the time to learn about FSMs, though, it was easy to
express the process as a series of states and transitions. I've since
found that most tricky UI code can be improved this way. Just a couple
months ago I was having trouble building a typeahead element from
scratch with [hoplon](http://hoplon.io/). Once I identified that the
difficulty was in keeping track of all the possible states, it only
took a few minutes drawing a state machine diagram and I was back on
track.

### Relational Algebra / SQL

I feel lucky that, during my fourteenth summer, I apparently had no
friends and so had nothing better to do than try and slog through
[a book on MySQL and the now-defunct mSQL](http://amzn.to/1RyFQ7z). You
can see from the reviews that the book "is sketchy, incomplete, and
almost totally useless." But, it did introduce me to SQL and
databases. Soon after, I learned relational algebra (the theory
underlying RDBMSs) and that investment has been one of the best of my
life. I can't count the number of times a `LEFT OUTER JOIN` has saved
my bacon. Friends be damned!

Learning relational algebra provided the foundation I needed to move
easily from MySQL to Oracle and MS SQL Server when I joined
EnterpriseCo, and in general knowing how to interact with databases
without a framework or ORM helped me quickly advance career-wise. It's
why, at 20, I was able to land a contract building a custom site for
the city of Santa Fe, New Mexico, instead of just cobbling together
Wordpress and Drupal plugins.

If you come from Rails or some other framework that handles all the
database interaction for you, one of the best things you can do for
your career is to learn relational theory and SQL. Read
[a book by C. J. Date](http://amzn.to/1Rm6WzL).

### The Unix Process Model

Understanding Unix processes helped me understand what's actually
happening when I run a program. It's also helped me understand what
exactly a web server is and what I'm doing when I write a web
application. The book Advanced Linux Programming has
[a chapter on processes for free online](http://advancedlinuxprogramming.com/alp-folder/alp-ch03-processes.pdf). Actually,
the whole book is free.

When you don't know about processes, programming is much harder and
more mysterious. It's harder to understand performance, and it's
harder to understand how programs interact with each other. If you
ever feel a vague sense that you don't really get what's going when
you run the apps you write, learning the process model will go a long
way toward clearing things up.

### Emotion Management

In my personal life I'm constantly learning about and practicing ways
to manage emotions. This stems from both my personal aspiration to
improve the lives of others and from the completely selfish reason
that it helps me do good work. Emotion management is probably the most
important meta-skill you can develop. I mean, emotions are at the core
of who you are as a human being.

The book [Non-Violent Communication](http://amzn.to/1Rm6EZE) is an
excellent resource for dealing with emotions. Also, my friend Alex
Harms recently wrote
[a book specifically for technical people](http://empathetictechnicalleader.com/).


Those are my programming power tools - I hope you find them useful!
