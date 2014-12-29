---
title: Aikidoka Prevents Namespace Collisions
created_at: May 10 23:23:00 -0500 2009
kind: article
categories: programming
summary: A way to avoid namespace collisions.
additional_stylesheets:
  - pygments
---

Recently I fell victim to the Twitter-Mash / Extlib-DataMapper-Mash
[namespace collision](http://blog.zerosum.org/2009/4/17/why-namespaces-are-important). To
get around this problem, I've created a new gem,
[Aikidoka](http://github.com/flyingmachine/aikidoka/tree/master).

Here's what happened when I tried to use Twitter when Extlib had
already been loaded:

```ruby
require 'Twitter'
# => ["Twitter"]
Twitter::Search.new("bokken").fetch
# SystemStackError: stack level too deep
```

Here's what happens when you use Aikidoka:

```ruby
require 'aikidoka'
# => ["Aikidoka"]
Aikidoka.rename("Mash" => "Twitter::Mash"){require 'twitter'}
# => ["Mash"]
Twitter::Search.new("aikidoka").fetch
# <Mash completed_in=0.052875 max_id=1754360060 next_page="?page=2&max_id=1754360060&q=aikidoka">
```

It works! What this does is namespace the Mash defined when I require the Twitter gem, so that Mash is now Twitter::Mash. Also, Extlib's Mash is still there, untouched, so you don't need to worry about that. Here's how Aikidoka does its magic:

* It temporarily renames existing constants so that they don't get
  clobbered. In this case, "Mash" is renamed to "AikidokaMash". Right
  now this only works with top-level constants.
* It yields to the given block. This block should define the constants
  you want permanently renamed/namespaced. In this case, we're
  requiring "twitter", which in turn requires "mash". "mash" defines
  the constant we want to rename, Mash.
* It creates modules as necessary to create the namespace. In this
  case, the module Twitter is already defined so that's used. However,
  if we wanted to rename "Mash" to "Potatoes::Mash", then a module
  named "Potatoes" would have been created.
* It assigns the object referred to by the old constant to its new
  constant. "Twitter::Mash" now refers to the same object that "Mash"
  refers to.
* Old constants are removed to clean up the namespace. The constant
  "Mash" no longer exists, the object it used to refer to lives on.
* The constants temporarily renamed in step 1 are now given their
  original names back. Extlib's "Mash" is no longer "AikidokaMash";
  it's "Mash" again.

The code is very simple - a total of 67 lines in one file with decent
specs - so hopefully it's easy to dig into.

Right now Aikidoka is best at nesting an existing top-level constant
within another constant of a different name. I haven't tried doing
something like `Aikidoka.rename("Mash" => "Mash::Twitter")` or
`Aikidoka.rename("ActiveRecord::Base" => "ARBase")`, and those
examples probably wouldn't work.

All in all, it does what I want it to and seems to work OK :) You can
install it with `gem install flyingmachine-aikidoka`. If you're
wondering about the name, aikido is a martial art designed to resolve
conflict harmoniously, and an aikidoka is a student of aikido.
