---
title: Look, Ma! No batteries! A Clojure Web App Without Noir
created_at: Sat Nov 10 2012 13:23:00 -0500 2012
kind: article
categories: programming
summary: "Having put together a website using Noir, I wanted to to try and get closer to the metal. Here are some of my findings, including: templating heaven with Enlive and Middleman; using macros to enforce full stack consistency; roll-your-own-validations; more!"
additional_stylesheets:
  - pygments
---

*UPDATE* [http://gratefulplace.com](http://gratefulplace.com) now uses
a completely different codebase. You can find it on
[github](https://github.com/flyingmachine/gratefulplace2). You can
read about the new codebase at [Building a Forum with Clojure,
Datomic, Angular, and
Ansible](/programming/building-a-forum-with-clojure-datomic-angular/).

Awhile back I released
[OMG! SMACKDOWN!!!](/programming/dissecting-omgsmackdown/), which
still holds the title for dumbest Clojure app in existence. To make up
for that ludicrostrosity, I've created
[Grateful Place](http://gratefulplace.com). This time I around I
didn't use Noir, instead going closer to the metal with Compojure and
Enlive. The experience was a lot easier than I anticipated, perhaps
easier than using Noir. This post will cover some of the more
interesting bits of code:

* Templating heaven with Enlive and Middleman
* Using macros to enforce full stack consistency
* Roll-your-own validations
* Pagination is more fun with macros
* Using fierce mustaches to run tasks
* Fun utilities

You can find all source code on
[github](https://github.com/flyingmachine/gratefulplace/tree/v1.0.0). Also,
I'm going to be at the Clojure Conj and would love do meet folks. If
you'd like to chat, please do tweet me (@nonrecursive) or find me at
the conj!

## App Overview

Grateful Place is a social app with only a few models: Users,
Comments, Posts, and Likes. Nothing too fancy. If you're interested in
why I made it, here's a [post I wrote about
gratitude](/essays/gratitude/).

## Templating heaven with Enlive and Middleman

### Enlive is the bee's knees

I can say without reservation that
[Enlive]:(https://github.com/cgrand/enlive) is the best templating
library I've ever used. I absolutely love it! If you try out one thing
from this post, try out the workflow I describe here. Enlive is
completely different from Hiccup, ERB, HAML and the like and it's
worth giving it a shot.

If you're not familiar with Enlive, it's a selector-based templating
library. In the same way that CSS allows you to separate structure and
presentation, Enlive lets you separate structure and view logic. This
is huge! Unless you're the kind of person who still likes to use
@font@ and @bold@ tags in your HTML, you should give Enlive a try.

### Middleman/Enlive Workflow

Using Enlive allowed me to do all my design work using the Ruby
library [Middleman](http://middlemanapp.com/). This allowed me to
build stand-alone web pages so that I could see how they looked
without worrying about any view logic. For example, here's the example
home page I put together:

```
---
title: Grateful Place
---
#posts
  .post
    .content
      %p Here is some content.
    %footer
      .date Jan 03, 2012
      .author
        by
        %a{:href => ""} Joe Schmoe
      .favorite
        %a{:href => "#"} &#9733;
        .status Like this
      %a.comments{:href => ""} 35 Comments
      

  .post
    .content
      %p Here is some content, too.
    %footer
      .date Jan 03, 2012
      .author
        by
        %a{:href => ""} Joe Schmoe
      .favorite
        %a{:href => "#"} &#9733;
        .status Like this
      %a.comments{:href => ""} 35 Comments

  .pagination
    %a.previous{:href => "#"}
      Previous  
    %span.page-link.current
      1
    %a.page-link{:href => "#"}
      2
    %a.page-link{:href => "#"}
      3
    %a.next{:href => "#"}
      Next
```

You might have noticed a few weird things. First, this is written
using HAML. Yep, HAML. I prefer HAML over HTML because it's more
concise and the consistent structure allows me to visually scan it
more easily. And since my HTML templates and view logic are completely
separate, I can write my designs in HAML, preview them with Middleman,
and then generate an HTML file for Enlive to work with. Here's my
process in full:

* Build on every change by cd'ing to `assets/generator/` and running
  `fswatch . ./g`. This runs a bash script which generates the HTML,
  CSS, and Javascript files and moves them to the proper directories.
* Start up Middleman by cd'ing to `assets/generator/` and running
  `middleman`
* View site at http://localhost:4567 and make changes to my heart's
  content
* When I'm happy with the changes, create or recompile a Clojure file
  which makes use of the HTML file with Enlive. It's necessary to
  recompile the files so that Enlive uses the updated HTML. That
  doesn't happen automatically.

Another thing you might have noticed is that in the HAML file I'm
showing two example posts. With Enlive, you actually have to add some
logic to remove one of the post divs:

```clojure
(defpage all "index.html"
  [posts current-auth record-count page max-pages]
  ;; don't show the second post as it's just an example
  [[:.post (h/nth-of-type 2)]] nil
```

### Some code to reduce boilerplate

Finally, here's some code I put together to reduce boilerplate:

```clojure
(defonce template-dir "gratefulplace/templates/")

(h/defsnippet nav (str template-dir "index.html") [:nav]
  [logged-in]
  [:.auth] (if logged-in
             (h/do-> (h/content "Log Out")
                     (h/set-attr :href "/logout"))
             (h/do-> (h/content "Log In")
                     (h/set-attr :href "/login"))))

(h/deftemplate layout (str template-dir "index.html")
  [html]
  [:html] (h/substitute html)
  [:nav] (h/substitute (nav (current-authentication)))

  [:nav :ul.secondary :#logged-in :a]
  (if-let [username (:username (current-authentication))]
    (h/do->
     (h/content username)
     (h/set-attr :href (str "/users/" username))))
  
  [:nav :ul.secondary :#logged-in :span]
  (when (current-authentication)
    (h/content "Logged in as")))

;; Need to come up with better name
;; Bundles together some defsnippet commonalities for user with the
;; layout template
;;
;; TODO destructuring doesn't work in argnames
(defmacro defpage
  [name file [& argnames] & body]
  `(do
     (h/defsnippet ~(symbol (str name "*")) (str template-dir ~file) [:html]
       [~@argnames]
       ~@body)
     (defn ~name
       [{:keys [~@argnames]}]
       (layout (~(symbol (str name "*")) ~@argnames)))))
```

The ultimate purpose of this code is the `defpage` macro. This helps
me in a few ways. It lets me easily write view code for each page
without having to worry about the surrounding layout. For example,
before writing the above code I had to include the navigation
transformations in every page, which quickly got annoying.

The `defpage` macro also bundles up the `template-dir` variable,
saving me some keystrokes and some duplication. Finally, it
establishes a consistent way of interacting with the controller, a
topic I'll explore more in the next section. Before that, here's an
example of `defpage` in action:

```clojure
(defpage show "posts/show.html"
  [post comments current-auth]
  [:.post :.author :a]   (linked-username post)
  [:.post :.date]        (h/content (created-on post))
  [:.post :.content]     (md-content post)

  [:.post :.edit]        (keep-when (can-modify-record? post))
  [:.post :.edit :a]     (set-path post post-edit-path)

  

  [:.post :.moderate]    (keep-when (moderator? (:username current-auth)))
  [:.post :.moderate :a] (h/do->
                          (set-path post post-path)
                          (h/content (if (:hidden post) "unhide" "hide")))
  
  [:#post_id]            (h/set-attr :value (:id post))
  [:.favorite]           (favorite current-auth post)
```

Thanks to `defpage`, my view code contains only logic specific to that
view. I love it!


## Using macros to enforce full-stack consistency

### Creating a route->controller interface

When I was putting the app together, each of my compojure routes was a
unique and beautiful snowflake:

```clojure
  (GET  "/users/new" []
        (users/show-new))
  
  (POST "/users" {params :params}
        (users/create! params))

  (GET  "/users/:username" [username]
        (users/show username))
```

As you can see, each route captures variables in a completely
different way. At first I thought this made sense; I was passing
exactly the data that was needed to each controller.

However, this quickly became a pain in the ass. For example, if I
wanted to make a request param available to a view, I had to modify
functions all up and down the stack: in the routes, in the controller
function, and in the view function. I ended up making all routes send
the full request object to their functions:

```clojure
(defmacro route
  [method path & handlers]
  `(~method ~path req#
            (->> req#
                ~@handlers)))

(defroutes routes
  (compojure.route/files "/" {:root "public"})

  ;; posts
  (route GET "/" posts/all)
  (route GET "/posts" posts/all)
  (route GET "/posts/new" posts/show-new)
  (route POST "/posts" posts/create! (friend/authorize #{:user}))
  (route GET "/posts/:id" posts/show)
  (route GET "/posts/:id/edit" posts/edit)
  (route POST "/posts/:id" posts/update)
```

The @route@ macro is very handy. It saves me from typing and (for the most part) from burning precious brain cells on thinking, and it enforces the "send the full request" constraint by not allowing me to deviate. Of course, I _can_ still deviate, and this is useful if you're using a library:

```clojure
  (friend/logout
   (ANY "/logout" []
        (ring.util.response/redirect "/")))
```

If I want to get to the params, I just use a `let`; no big deal:

```clojure
(defn show
  [req]
  (let [id (get-in req [:params :id])
```

### Creating a controller->view interface

In the same way that it was useful to create a route->controller
interface, it became useful to create a controller->view
interface. That was accomplished with this macro:

```clojure
(defmacro view
  "provides defaults for the map provided to view functions and allows
  you to provide additional key value pairs. Assumes that a variable
  named req exists"

  [view-fn & keys]
  `(let [x# {:current-auth (friend/current-authentication)
             :errors {}
             :params (:params ~'req)
             :req ~'req}]
     (~view-fn (into x# (map vec (partition 2 ~(vec keys)))))))
```

This also helped with one weird problem I ran into with cemerick's
friend library where `(friend/current-authentication)` returns nil
from within an anonymous function (and there are many anonymous
functions in my view code). I could circumvent this problem by
assigning doing something like `(let
[current-auth (friend/current-authentication)])`. But it seemed to
make more sense to just always pass that data along when calling view
functions, as you can see in the macro above.

The `defpage` macro also helped in establishing this interface in that
it allowed me to destructure the arguments sent by the controller. For
example, if a controller had

```clojure
(view posts/show {:post post})
```

I could create the corresponding view with

```clojure
(defpage show [post current-authentication])
```

And I wouldn't even have to worry about the order of the arguments. It
really reduced some "incidental complexity" (and I'm almost certainly
using that term incorrectly).

## Roll-your-own validations

It wasn't too difficult to write my own way of doing validations, and
I'm satisfied with it for now. Here's a description of the data
structure, with an example following:

* Validation: a combination of a key and "validation check groups"
* Validation check group: first element is an error message and the
  rest are "validation checks". If any validation check returns false
  then the error message is added to a list of error messages for the
  given key.
* Validation check: a boolean function to apply to one of a record's
  values. The value corresponds to the validation key

Here's an example:

```clojure
(validate
 {:username "joebob", :password "hey"}
 {:password
   ["Your password must be at least 4 characters long"
    #(>= (count %) 4)]})
```

This validation would fail because the value `"hey"` for the
`:password` is not at least four characters long. Here's the main
validation code:

```clojure
(defmacro if-valid
  [to-validate validations errors-name & then-else]
  `(let [to-validate# ~to-validate
         validations# ~validations
         ~errors-name (validate to-validate# validations#)]
     (if (empty? ~errors-name)
       ~(first then-else)
       ~(second then-else))))

(defn error-messages-for
  "return a vector of error messages or nil if no errors
validation-check-groups is a seq of alternating messages and
validation checks"
  [value validation-check-groups]
  (filter
   identity
   (map
    #(when (not ((last %) value)) (first %))
    (partition 2 validation-check-groups))))

(defn validate
  "returns a map of errors"
  [to-validate validations]
  (let [validations (vec validations)]
    (loop [errors {} v validations]
      (if-let [validation (first v)]
        (let [[fieldname validation-check-groups] validation
              value (get to-validate fieldname)
              error-messages (error-messages-for value validation-check-groups)]
          (if (empty? error-messages)
            (recur errors (rest v))
            (recur (assoc errors fieldname error-messages) (rest v))))
        errors))))
```

`if-valid` wasn't strictly necessary but I made it and ended up liking
it. It's a small improvement, but a handy one.

## Pagination is more fun with macros

It was a bit difficult for me to pull the pagination code together. I
tried many approaches, and I'm not 100% satisfied with what I have,
but it's better than nothing! The basic approach was to add a couple
Korma clauses to a base set of clauses. Here are the base clauses,
along with a macro that allows me to add more clauses willy-nilly:

```clojure
(def base
  (->
   (select* e/post)
   (with e/user
         (fields :username))
   (with e/comment
         (aggregate (count :*) :count)
         (where {:hidden false}))
   (with e/favorite
         (aggregate (count :*) :count))
   (order :created_on :DESC)))

(defmacro all
  [& clauses]
  `(-> base
       ~@clauses
       select))
```

Here's the pagination macro:

```clojure
(defmacro paginate
  ([page num-per-page query]
     `(~@query
       (limit ~num-per-page)
       (offset (dec ~page)))))
```

And here's the whole thing in action:

```clojure
(defn all
  [req]
  (let [current-auth (friend/current-authentication)
        per-page 20
        page (str->int (or (get-in req [:params :page] 1)))
        conditions (with-visibility
                     current-auth
                     {:moderator true
                      :logged-in (or {:hidden false}
                                     {:user_id [= (:id current-auth)]})
                      :not-logged-in {:hidden false}})
        record-count (post/record-count (where conditions))]
    (view
     view/all
     :posts (paginate page per-page (post/all (where conditions)))
     :record-count record-count
     :page page
     :max-pages (ceil (/ record-count per-page)))))
```

I definitely think there's room for improvement here and would love
suggestions.

## Using fierce mustaches to run tasks

So it turns out that you can use leiningen to run tasks, kind of like
Rake. As far as I know, it's not as powerful in that you can't specify
dependencies, but it's still better than nothing! Here's a file I
wrote to allow me to rebuild and re-seed by db:

```clojure
(ns tasks.db
  (:refer-clojure :exclude [alter drop complement
                            bigint boolean char double float time])
  (:require [gratefulplace.models.user :as user])
  (:use (lobos core connectivity)))

(defn rebuild
  []
  (rollback :all)
  (migrate))

(defn seed
  []
  (println (user/create! {:username     "higginbotham"
                          :email        "daniel@flyingmachinestudios.com"
                          :display_name "higginbotham"
                          :password     "password"})))
(defn -main
  [task-name]
  (condp = task-name
    "rebuild" (rebuild)
    "seed" (seed)))
```

I ran `lein run -m tasks.db rebuild` and `lein run -m tasks.db seed`.

### Fun utilities

Here's some stuff that was fun to write:

```clojure
(ns gratefulplace.utils)

(defn str->int
  ([str]
     (Integer.  str))

  ([m & keys]
     (reduce
      (fn [m k]
        (if-let [val (k m)]
          (assoc m k (str->int val))
          m))
      m keys)))

(defn deserialize
  [m & ks]
  (reduce #(assoc % %2 (read-string (%2 %))) m ks))

(defmacro self-unless-fn
  [self fn otherwise]
  `(let [self# ~self]
     (if (~fn self#)
       ~otherwise
       self#)))
```

## The End

And that's it! I hope you've found this useful. I'd love any feedback,
so please do leave a comment or tweet me or email me. And like I
mentioned above - it'd be great to meet some folks at the Conj next
week!
