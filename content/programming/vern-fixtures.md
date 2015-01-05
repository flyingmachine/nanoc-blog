---
title: Vern, a Library For Database Fixtures
created_at: Thu Dec 4 2014
kind: article
categories: programming
additional_stylesheets:
  - pygments
summary: "I've started writing a little library for writing and loading database fixtures and I'd love any feedback :)"
---

At work, I've been using
[yesql](https://github.com/krisajenkins/yesql) and wanted a way to
compactly describe some seed data. My first attempt was an utter
abomination, but I think I've worked out something pleasant that could
be useful to other
people. [vern](https://github.com/flyingmachine/vern) is a 54-line
project that I'd love to get feedback on before publishing. It's named
after one of my cats, this guy:

![vern](/assets/images/posts/vern-fixtures/vern.jpg)

## Code

Here's an example of vern in use:

```clojure
(def fixtures [:endpoints
               [:wallace
                {:name "wallace" :id 1}

                :gromit
                {:name "gromit"  :id 2}]

               :permissions
               [;; these permissions belong to wallace
                [{:endpoint [:endpoints :wallace]}

                 :wallace-read
                 {:name "read"  :id 10}

                 :wallace-write
                 {:name "write" :id 11}]

                ;; these permissions belong to gromit
                [{:endpoint [:endpoints :gromit]}

                 :gromit-read
                 {:name "read"  :id 12}

                 :gromit-write
                 {:name "write" :id 13}]]])

(def entities (atom []))

(do-named (fn [processed group-name entity]
              (swap! entities #(conj % (:data entity)))
              (get-in entity [:data :id]))
            fixtures)

@entities
; =>
[{:id 1 :name "wallace"}
 {:id 2 :name "gromit"}
 {:id 10 :endpoint 1 :name "read"}
 {:id 11 :endpoint 1 :name "write"}
 {:id 12 :endpoint 2 :name "read"}
 {:id 13 :endpoint 2 :name "write"}]
```

First, we assign the fixture the colorful name `data`, then create a
"database", which in this case is just an atom holding an empty
vector.

`do-named` is where the real action happens. `processed` is a map of
all the named entities that have been processed so far. For example,
after processing the first two entities, the value of `processed` will
be

```clojure
{:endpoints {:wallace 1
             :gromit 2}}
```

Most likely you won't need it but it's there in case you
do. `group-name` will be `:endpoints` and then `:permissions` in this
example. In real life, I use this to determine which table I need to
insert the entity's data into. `entity` will be something like

```clojure
{:name :wallace
 :data {:name "wallace" :id 1}}
```

In the example, you're just using conjing `(:data entity)` onto the
`entities` atom's vector. Here's what I do in real life, though:

```clojure
(defn load-fixtures
  [db fixtures]
  (d/do-named (fn [processed group-name entity]
                (:generated_key (q/insert! group-name db (:data entity))))
              fixtures))
```

This uses yesql's functionality to insert a row in a database and
return the value of the key that was generated for every entity in the
fixture. The reason you want to return the generated key is so that
you can refer to this record in later entities. If a subsequent entity
is defined as

```clojure
{:name "belongs-to-wallace"
 :endpoint-id [:endpoints :wallace]}
```

then `do-named` uses the corresponding value in the `processed`
map. When `do-named` sends the first "permission" entity to its
function, the entity looks like this:

```clojure
{:name :wallace-read
 :data {:name "read" :id 10 :endpoint 1}}
```

The last feature is that you can group common attributes together in a
vector. You can see this in the permissions:

```clojure
[[{:endpoint [:endpoints :wallace]}

  :wallace-read
  {:name "read"  :id 10}

  :wallace-write
  {:name "write" :id 11}]]
```

You could have written these entities as follows:

```clojure
[:wallace-read
 {:name "read"  :id 10
  :endpoint [:endpoints :wallace]}

 :wallace-write
 {:name "write" :id 11
  :endpoint [:endpoints :wallace]}]
```

But that kind of repetition can pretty old pretty quickly.

Here's the general form of the fixtures:

```clojure
[:entity-group-key
 [:entity-1-key ;; entity keys are optional
  {:name "zip"}

  ;; using a sequential structure allows you to reference
  ;; another entity
  {:parent-id [:entity-group-key :entity-1-key]
   :name "zip's child 1"}

  ;; you can define common associations by grouping entities in a
  ;; sequential; the first element contains the common associations
  [{:parent-id [:entity-group-key :entity-1-key]}
   {:name "zip's child 2"}
   {:name "zip's child 3"}]]

 :entity-group-2
 [{:entity-group-1-id [:entity-group-key :entity-1-key]}]]
```

## Request for Feedback

This is a
[tiny library](https://github.com/flyingmachine/vern/blob/master/src/com/flyingmachine/vern.clj),
and I hope it might yield a couple fun rounds of code golf. In
particular, I have the sneaking suspicion that I'm neglecting some
useful functions from Clojure's standard library. I considered using
`zip` but that seemed too heavyweight. Also, I think I could just pass
the entity's data to the `do-named` function.

I'm also curious if there's a name for this kind of pattern, where you
aren't solely processing one sequence item at a time but care about
what you last processed as well (this is how names get assigned to
entities).
