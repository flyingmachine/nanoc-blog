---
title: WTF is Datomic? (And Should You Care?)
created_at: Feb 2 9:23:00 -0500 2009
kind: article
categories: programming
summary: Datomic is a fairly new database system that could hasn't been explained well. This is a brief conceptual introduction.
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
you will have a solid conceptual grasp of the three key ways that
Datomic is unique. You will also understand key Datomic terms. This
will make it much easier to actually get your hands dirty with Datomic
if you decide to investigate it further.

## Overview of Datomic's Three Righteous Pillars of Databasing

Datomic differs from existing solutions in its _information model_,
its _architecture_ and its _programmability_. Below is a quick
overview followed by a more detailed explanation of each facet.

<table>
    <tr>
        <th></th>
        <th>Relational DB</th>
        <th>Schemaless DB</th>
        <th>Datomic</th>
    </tr>
    <tr>
        <td>Information Model</td>
        <td>The unit of information is an entity in a relation and you
            alter it in place, forgetting previous values</td>
        <td>The unit of information is a schemaless document and you
            update it in place, forgetting previous values</td>
        <td>The unit of information is a fact comprised of an entity,
            attributes, values, and time. You accrete the assertion and
            retraction of facts instead of updating in place.</td>
    </tr>
    <tr>
        <td>Architecture</td>
        <td>Database is a monolithic system responsible for querying,
            ACID compliance, and storage</td>
        <td>Same as RDBMS</td>
        <td>Separate querying, transacting, and storage in such a way
            that you get ACID compliance, read scalability, and more
            power within the application using Datomic</td>
    </tr>
    <tr>
        <td>Programmability</td>
        <td>SQL - applications abstract SQL by performing string
            manipulation</td>
        <td>Proprietary data structures which are more
            programming-friendly than SQL but less powerful</td>
        <td>Datalog, completely uses data structures and has as much
            power as SQL</td>
    </tr>
</table>

## First Righteous Pillar of Databasing: Information Model

A database's information model is defined by its rules regarding the
way entities and attributes relate to each other -- for lack of a
better term, its schema system.

Yea, the choice between a relational and schemaless database is
probably your primary concern in choosing a database because of its
influence on how you write and grow your programs. You'll see that
Datomic's schema system isn't as rigid as the relational model but
affords more power than a schemaless database.

Additionally, a database's information model is defined by its
approach to time. Datomic's approach to time different from most, if
not all, existing databases.

Below are the schema systems for relational dbs, schemaless dbs, and
datomic, followed by a comparison of the way these systems handle time.

### Relational Schemas

You're probably already familiar with the
[relational model](http://en.wikipedia.org/wiki/Relational_model).
Here are key definitions:

<table>
    <tr>
        <td>Entity</td>
        <td>An entity is <em>tuple</em> within a <em>relation</em>. It
        is comprised of a fixed set of attributes. In practice, an
        entity is a row in a table.
    </tr>
    <tr>
        <td>Attribute</td>
        <td>A name + a data type. A column in a table. Attributes do not
        exist outside of relations. Attributes in different relations
        are always logically distinct, even if they hold the same
        type of data.</td>
    </tr>
</table>

![Relational](/assets/images/posts/datomic/relational.png)

Probably the most important fact about the relational model is that
it's rigid. Every entity must belong to a rigid relation. There's no
way to add or remove attributes except by altering the structure of
the relation, which in turn alters every other entity in that relation.

### Schemaless 

Schemaless (NoSQL) databases were created to address the rigidity of
relational databases. They offer a few facilities for organization -
for example, collections in MongoDB - but forego any structure for
entities. This lack of structure comes at a cost: it limits your query
power and forces data integrity concerns into your application.

<table>
    <tr>
        <td>Entity</td>
        <td>For our purposes, a document. A document has no
        restrictions on what attributes it can have.</td>
    </tr>
    <tr>
        <td>Attribute</td>
        <td>A name. Attributes can hold values of any type, and an attribute
        belonging to one entity is logically distinct from all other
        attribute, even other entities belonging to the same
        collection.</td>
    </tr>
</table>

![Schemaless](/assets/images/posts/datomic/schemaless.png)

### Datomic Schemas

In Datomic, a schema defines a core set of attributes which
effectively act as data types. An entity can possess any attribute
without restriction. In this way, entities have more structure than a
schemaless database but are more flexible than in relational
databases. Additionally, you retain the query power of the
relational model without having to handle data integrity concerns in
your client applications.

<table>
    <tr>
        <td>Entity</td>
        <td>A map of attribute/value pairs. Entities have no fixed
        shape; they can be comprised of any attributes defined in the
        schema. </td>
    </tr>
    <tr>
        <td>Attribute</td>
        <td>Name + data type + cardinality. Attributes themselves can
        be thought of as data types. They differ from attributes in
        the relational model in that they exist outside of rigid tables</td>
    </tr>
</table>
        
![Datomic](/assets/images/posts/datomic/datomic.png)

### Time

Datomic's notion of time might be unfamiliar for those unacquainted
with Rich Hickey's thoughts on time and how it relates to identity,
value, and state. To get up to speed, check out my article,
["The Unofficial Guide to Rich Hickey's Brain"](/programming/the-unofficial-guide-to-rich-hickeys-brain/)

In both relational and schemaless databases, there's no built-in
notion of time. It's always "now" - the database doesn't hold on to
the past. Whenever you update or delete a row, you have no way of
retrieving previous states for that row.

This is the source of many headaches for us. If you've ever used or
any code that requires multiple trips to the database in order to
perform one unit of work, you know what I mean. In between each trip,
you have no way of knowing whether the database has been been altered.
You can end up writing all kinds of hacks to avoid race conditions and
still not end up with a bulletproof system.

In addition, it's virtually impossible to query past states. You might
not think this is really matters because you're used to the
limitation, but being able to easily query past states is very
powerful.

In Datomic, time is a first-class concept. All changes to the database
are associated with a transaction, and each transaction has a
timestamp. Rather than updating an entity in place and "forgetting"
its previous state, datomic "accretes" states. In this way, you're
able to retrieve all previous states for an entity.

![Datomic](/assets/images/posts/datomic/time.png)

Another way of thinking about it is that the database is an identity
which we superimpose on a time-ordered collection of values. In this
line of thinking, a "value" is the database as whole - its collection
of entities and attributes. When you run a transaction which creates,
updates, or deletes entities, you create a new database value. You're
always able to say, "I want to work with the value of the database at
this point in time."

## Architecture

Existing databases couple the following into one monolithic:

* Reading
* Writing
* Storage

Datomic decouples these capabilities. All writing is handled by a
single "transactor", ensuring ACID compliance. This is the only point
of similarity to existing solutions.

Querying happens within "peers" - basically, running processes which
make use of Datomic's peer library. The peer library is responsible
for connecting to and querying your database. In this way, your
queries run largely in your application rather than in a central
database server, allowing for easy read scalability. I think the
Datomic dudes were trying to avoid the term "client" here, but a peer
is essentially a client.

Finally, Datomic does not implement its own storage solution but
instead relies on storage as a service, allowing you to choose which
storage backend you use. Right now, the following backends are
supported:

* The filesystem
* DynamoDB
* Riak
* Couchbase
* Infinispan

## Programmability

In Datomic, everything is data! Your schema is data! Queries are data!
Transactions are data! This is great because it's easier to manipulate
data structures than it is to perform crazy string concatenation like
you do with SQL. Check out
[these example queries](https://github.com/Datomic/day-of-datomic/blob/master/samples/seattle/getting-started.clj).

## Resources

* [The Design of Datomic](http://www.infoq.com/presentations/The-Design-of-Datomic)
* [Rich Hickey on Datomic](http://www.infoq.com/interviews/hickey-datomic)
* [The Value of Values](http://www.infoq.com/presentations/Value-Values)
* [The Datomic Information Model](http://www.infoq.com/articles/Datomic-Information-Model)

## The End

That's it for now! If you have any suggestions please let me know. As
you can tell by the decreasing length of each major section, I got
pretty tired of writing by the end of the article. If anything doesn't
make sense I'll try to clarify.
