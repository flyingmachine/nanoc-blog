---
title: WTF is Datomic? (And Should You Care?)
created_at: Feb 2 9:23:00 -0500 2009
kind: article
categories: programming
summary: Datomic is a fairly new database system that has thus far been terribly explained. This is a brief conceptual introduction.
draft: true
---

If you're interested in
[Datomic](http://docs.datomic.com/tutorial.html), you may have been
deterred from checking it out because you lack the enthusiasm
necessary to slog through
[3 keynotes, 8 interviews, and innumerable walls of text](http://www.infoq.com/search.action?queryString=datomic&searchOrder=relevance&search=datomic)
in order to get a basic idea of what the hell it is and whether you
should use it.

Well, good news! I've done all that for you! How could I resist your
winning smile, you charmer you? After you're done with this article,
you will have a solid conceptual grasp of the three main ways that
Datomic is unique. You will also understand key Datomic terms. This
will make it much easier to actually get your hands dirty with Datomic
if you decide to investigate it further.

## Overview of Datomic's Three Pillars of Badassitude

Datomic is unique in many ways, but the following features are key to
its power. I'll explain each in more detai below, contrasting each
with relational databases and document databases.



* The kinds of problems Datomic solves
* The three ways in which Datomic is unique and exciting. Spoiler:
    * It has a distinct *information model*. It's not a relational
      database and it's not a document database - it's something
      entirely new!
    * It is completely data-driven. For example, rather than using SQL
      strings to query the database, queries are simple data structures
    * Its modern, architecture allows for easy read scalability while
      maintaining the data integrity benefits of ACID trnsactions
* The steps involved in reading from and writing to the database
* What you should do 

## Further Reading

* [The Design of Datomic](http://www.infoq.com/presentations/The-Design-of-Datomic)
