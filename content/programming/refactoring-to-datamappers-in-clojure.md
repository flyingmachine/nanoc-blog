---
title: Refactoring to Datamappers Clojure
created_at: Fri Jul 06 7:47:00 -0500 2012
kind: article
summary: In this post I give a detailed description of a recent refactoring for my site <a href="http://omgsmackdown.com">OMG! SMACKDOWN!!!</a> . I make no attempt to enliven the article with "cats" or "memes" or "humor" - it's straight up code and commentary.
additional_stylesheets:
  - pygments
---

In this post I give a detailed description of a recent refactoring for
my site ["OMG! SMACKDOWN!!!"](http://omgsmackdown.com). I make no
attempt to enliven the article with "cats" or "memes" or "humor" -
it's straight up code and commentary.

The refactoring was largely influenced by Rich Hickey's talk
["Simplicity Matters"](http://www.confreaks.com/videos/860-railsconf2012-keynote-simplicity-matters). The
main purposes were:

* Reduce complexity in the Hickeysian sense - untangle bits of code
  that don't need to be tangled together
* Introduce an explicit
  ["design"](http://www.flyingmachinestudios.com/design/anatomy-of-frustration/)
* Reduce repetition

In order to do this, I kept the following guidelines in mind:

* Create clear separation between data and the functions which transform them
* Reduce coupling through information and implementation hiding
* Abstract out the "bones" of functionality

In the process I may have created an even more terrible monster than
the one I started with and perhaps lost a few
["sanity points"](http://www.amazon.com/gp/product/1589942108/ref=as_li_ss_tl?ie=UTF8&tag=aflyingmachin-20&linkCode=as2&camp=1789&creative=390957&creativeASIN=1589942108)
but the process was fun and educational.

## Quick Project Overview

["OMG! SMACKDOWN!!!"](http://omgsmackdown.com/), perhaps one of the
most important web sites in Internet history, is a
["Noir"](http://webnoir.org/) project which allows users (well, just me
for now) to create `arenas` wherein `fighters` `battle` it out.

The project code accomplishes the following:

* Show two randomly selected fighters from different teams on the home page
* Record each click on a fighter as a "win" for that fighter
* Allow me to create, update, and delete arenas
* Allow me to create, update, and delete fighters, including their images which get uploaded to S3

It uses ["clj-aws-s3"](https://github.com/weavejester/clj-aws-s3) and
["Clojure Monger"](http://clojuremongodb.info/).

All the code discussed here is
["available on github"](https://github.com/flyingmachine/nanoc-blog/tree/master/content/assets/source/clojure/refactoring-to-datamappers).

## The Original Monster

Below is the most complex of my models:

```clojure
(ns arenaverse.models.fighter
  (:require [arenaverse.config :as config]
            [monger.collection :as mc]
            [arenaverse.lib.aws.s3 :as s3])

  (:import [org.bson.types ObjectId]
           [org.imgscalr Scalr Scalr$Method Scalr$Mode]
           [javax.imageio ImageIO]
           [java.awt.image BufferedImageOp]
           [java.awt.image BufferedImage]
           [org.apache.commons.io FilenameUtils]))

;; Used to generate the different sizes
(def *image-versions [["card" 192]
                      ["listing" 64]
                      ["battle" 432 638]])

;; For monger
(def *collection "fighters")

;; ----
;; Image processing and storage
;; ----

;; The image-path functions are used both to store the image in S3 and
;; to generate the URL for the image when viewed on the web
(defn image-relative-path [version {:keys [_id image-extension]}]
  (str "fighters/" _id "/" version "." image-extension))

(defn image-path [version record]
  (str "/" (image-relative-path version record)))

(defn bucket-name []
  (str "arenaverse-" (name config/env)))

(defn amazon-image-path [version record]
  (str "https://s3.amazonaws.com/" (bucket-name) (image-path version record)))

(defn- image-fields [object-id image-extension]
  {:_id object-id
   :image-extension (clojure.string/replace image-extension "jpeg" "jpg")})

(defn- save-image [path file content-type]
  (s3/put-object config/*aws-credentials*
                 (bucket-name)
                 path
                 file
                 {:content-type content-type}
                 #(.withCannedAcl % com.amazonaws.services.s3.model.CannedAccessControlList/PublicRead)))

(defn- resize
  ([image box]
     (resize image box box))
  ([image target-width target-height]
     (let [width  (.getWidth image)
           height (.getHeight image)
           fit (if (> (/ target-width width) (/ target-height height)) Scalr$Mode/FIT_TO_HEIGHT Scalr$Mode/FIT_TO_WIDTH)]
       (Scalr/resize image Scalr$Method/ULTRA_QUALITY fit target-width target-height (into-array BufferedImageOp [])))))

;; This is necessary because the S3 java library works with input
;; streams
(defn- buffered-image->input-stream [buffered-image extension]
  (let [os (java.io.ByteArrayOutputStream.)]
    (ImageIO/write buffered-image extension os)
    (java.io.ByteArrayInputStream. (.toByteArray os))))

(defn- resize-and-save-image [object-id file-upload]
  (if (not (= 0 (:size file-upload)))
    (let [extension    (FilenameUtils/getExtension (:filename file-upload))
          content-type (:content-type file-upload)
          file         (:tempfile file-upload)
          img-fields   (image-fields object-id extension)]
      ;; save original
      (save-image (image-relative-path "original" img-fields) file content-type)
      (let [buff-img (ImageIO/read file)]
        (doseq [[version & dim] *image-versions]
          (save-image (image-relative-path version img-fields)
                      (buffered-image->input-stream (apply resize (cons buff-img dim)) extension)
                      version))))))

;;----
;; CRUD-related
;;---- 
(defn delete-images [record]
  (doseq [[vname] (conj *image-versions ["original"])]
    (s3/delete-object config/*aws-credentials* "arenaverse-test" (image-relative-path vname record))))

(defn create [attrs]
  (let [object-id (ObjectId.)
        file-upload (:file attrs)
        fields (merge
                (dissoc attrs :file)
                (image-fields object-id (FilenameUtils/getExtension (:filename file-upload))))]
    (mc/insert *collection fields)
    (future (resize-and-save-image object-id file-upload))
    fields))

(defn image-extension-for-update [attrs record]
  (let [file-upload (:file attrs)]
    (if (not= 0 (:size file-upload))
      (image-fields "" (FilenameUtils/getExtension (:filename file-upload)))
      (select-keys record [:image-extension]))))

(defn update [attrs]
  (let [_id (:_id attrs)
        bson-id (ObjectId. _id)
        record (mc/find-map-by-id *collection bson-id)
        updated-fields (dissoc (merge record
                                      attrs
                                      (image-extension-for-update attrs record))
                               :_id
                               :file)]
    (mc/update-by-id *collection bson-id updated-fields)
    (future (resize-and-save-image _id (:file attrs)))
    updated-fields))

(defn destroy [_id]
  (delete-images (mc/find-map-by-id *collection (ObjectId. _id)))
  (mc/remove-by-id *collection (ObjectId. _id)))

(defn one [& [query-doc]]
  (mc/find-one-as-map *collection query-doc))

(defn all [& [query-doc]]
  (mc/find-maps *collection query-doc))

(defn idstr [record]
  (.toString (:_id record)))
```

So, yeah, that's a ton of code. Breaking it into two groups,
image-handling and CRUD operations, might help to quickly grok it.

The main problem with the fighter model is that it has little explicit
structure. There's no design. It's all just "keep doing stuff until
you get the result you want." Sure, you have common functionality
refactored into separate functions. For instance,
`resize-and-save-image` is needed by both `create` and `update`, so it
gets its own function.

But that's not design. That's coding by
convenience. ["Well-designed"](http://www.flyingmachinestudios.com/design/anatomy-of-frustration/)
code is predictable code. It follows a pattern, giving you a headstart
when writing new code and allowing you to quickly assimilate
unfamiliar parts of the system.

To complete the picture, here's the arena model:

```clojure
(ns arenaverse.models.arena
  (:require [arenaverse.config :as config]
            [monger.collection :as mc])

  (:import [org.bson.types ObjectId]))

(def *collection "arenas")

(defn destroy [_id]
  (mc/remove-by-id *collection (ObjectId. _id)))

(defn all []
  (mc/find-maps *collection))

(defn one [& [query-doc]]
  (mc/find-one-as-map *collection query-doc))

(defn idstr [record]
  (.toString (:_id record)))
```

As you can see, the functions are essentially the same as those in the fighter model. Good ol' copy-and-paste coding. Clipboard-driven design. One of the cardinal sins of programming. Truly, I am ashamed.

## Enter the Hero: Datamapper

(Wow, check out that heading! This is getting epic! As befits an
article about a site as majestic and grand as OMG! SMACKDOWN!!!)

I have something embarrasing to admit. I've already revealed my
clipboard abuse, and nothing could be as bad that, so here goes: I
don't really know what a Model is. Sure, it's the "M" in MVC, it's a
staple of software design, it's something that's been in every Rails
(and merb! yeah I was one of those guys for awhile) project, large and
small, that I've been involved with since I first watched
["DHH's Danish-accented, whoops-laden screencast"](http://www.youtube.com/watch?v=Gzj723LkRJY).

But in writing the above code, I felt like I was throwing everything
into the `fighter` model out of habit. I kept asking myself, "what
_is_ a model, really?" I know, I know, I should stop with the soul
searching and get back to the code. What I'm getting at, though, is
that Clojure (and to some extent Noir) is really forcing me to
re-examine my current habits and assumptions about coding and software
design.

Here are the decisions I introduced to impose order:

### I'm dealing with "data mappers", not "models"

The code above concerns itself with:

* Transforming user input in order to persist it
* Persisting the transformed data
* Retrieving data from storage
* Transforming retrieved data for consumption by the system

This data-centric view of the world speaks more of lightweight, "dumb"
data structures than heavyweight, overly-smart "models". Perhaps the
distinction is all in my head, but by using the term "data mapper" I
want to communicate that the code's purpose is to act as a pipe - a
"map", if you will - between the domain layer and the persistence
layer.

### Data mappers should clearly distinguish data, transformation, and persistence

In the original `fighter.clj`, I mix together transformation and
persistence functions, as you can see below:

```clojure
(defn- resize-and-save-image [object-id file-upload]
  (if (not (= 0 (:size file-upload)))
    (let [extension    (FilenameUtils/getExtension (:filename file-upload))
          content-type (:content-type file-upload)
          file         (:tempfile file-upload)
          img-fields   (image-fields object-id extension)]
      ;; save original
      (save-image (image-relative-path "original" img-fields) file content-type)
      (let [buff-img (ImageIO/read file)]
        (doseq [[version & dim] *image-versions]
          (save-image (image-relative-path version img-fields)
                      (buffered-image->input-stream (apply resize (cons buff-img dim)) extension)
                      version))))))
```

I mean, the name is "resize-AND-save-image". Transform AND persist,
mooshed together. This looks like exactly the kind of "complecting"
that Rich Hickey talked about. I'm shoving two things together which
really don't belong together.

What I decided to instead was to be explicit about the persistence
process. Functions should transform input data into a representation
appropriate for the storage medium. Then a separate function should
store that representation. This way, the persistence function has no
knowledge of the transformation function and vice versa.

The same applies to data retrieval. First, retrieve a representation
from storage. Next, transform that data using a function. Return the
new representation.

One specific benefit I wanted to get out of this process was to remove
any need for domain code to translate between string ID's and
mongodb's BSON `ObjectIDs`. I had `(idstr fighter)` littered all
throughout my views, and that was ugly and unnecessary.

## The Refactored Code

Here's the refactored fighter.clj file:

```clojure
(ns arenaverse.data-mappers.fighter
  (:require [arenaverse.data-mappers.db :as db]
            [arenaverse.lib.aws.s3 :as s3]
            [arenaverse.config :as config])

  (:import [org.bson.types ObjectId]
           [org.imgscalr Scalr Scalr$Method Scalr$Mode]
           [javax.imageio ImageIO]
           [java.awt.image BufferedImageOp]
           [java.awt.image BufferedImage]
           [org.apache.commons.io FilenameUtils]))

(declare one-by-id)

(db/add-db-reqs)
(let [collection-name "fighters"]
  (db/add-db-fns collection-name)
  (db/add-finder-fns))

(def *image-versions [["card" 192]
                      ["listing" 64]
                      ["battle" 432 638]])

;;----
;; Images
;;----
(defn image-relative-path [version {:keys [_id image-extension]}]
  (str "fighters/" _id "/" version "." image-extension))

(defn image-path [version record]
  (str "/" (image-relative-path version record)))

;; TODO not hardcode this
(defn- bucket-name []
  (str "arenaverse-" (name config/env)))

(defn amazon-image-path [version record]
  (str "https://s3.amazonaws.com/" (bucket-name) (image-path version record)))

(defn- normalize-image-extension [extension]
  (clojure.string/replace extension "jpeg" "jpg"))

(defn- image-fields [object-id image-extension]
  {:_id object-id
   :image-extension (normalize-image-extension image-extension)})

(defn- resize
  ([image box]
     (resize image box box))
  ([image target-width target-height]
     (let [width  (.getWidth image)
           height (.getHeight image)
           fit (if (> (/ target-width width) (/ target-height height)) Scalr$Mode/FIT_TO_HEIGHT Scalr$Mode/FIT_TO_WIDTH)]
       (Scalr/resize image Scalr$Method/ULTRA_QUALITY fit target-width target-height (into-array BufferedImageOp [])))))

(defn- buffered-image->input-stream [buffered-image extension]
  (let [os (java.io.ByteArrayOutputStream.)]
    (ImageIO/write buffered-image extension os)
    (java.io.ByteArrayInputStream. (.toByteArray os))))

(defn- input->image-extension [input]
  (FilenameUtils/getExtension (:filename (:file input))))

(defn- image-uploaded? [input]
  (not (= 0 (:size (:file input)))))

(defn- input->images [input]
  (let [file-upload   (:file input)
        original-file (:tempfile file-upload)
        content-type  (:content-type file-upload)
        original-image {:version "original"
                        :file original-file
                        :content-type content-type}
        image-extension (normalize-image-extension (input->image-extension input))
        buff-img (ImageIO/read original-file)]
    
    ;; TODO make this a lazy seq
    (conj (map (fn [[version & dim]]
                 {:version version
                  :file (buffered-image->input-stream
                         (apply resize (cons buff-img dim))
                         image-extension)
                  :content-type content-type})
               *image-versions)
          original-image)))

(defn- store-image [image, record]
  (s3/put-object config/*aws-credentials*
                 (bucket-name)
                 (image-relative-path (:version image) record)
                 (:file image)
                 {:content-type (:content-type image)}
                 #(.withCannedAcl % com.amazonaws.services.s3.model.CannedAccessControlList/PublicRead)))


(defn- store-images [input db-fields]
  (future
    (doseq [image (input->images input)]
      (store-image image db-fields))))

(defn- create-input->db-fields [input]
  (let [object-id (ObjectId.)]
    (merge
     (dissoc input :file)
     (image-fields object-id (input->image-extension input)))))

(defn- update-input->db-fields [input]
  (let [object-id (ObjectId. (:_id input))
        record (db-one-by-id object-id)
        ;; ensure that the user doesn't alter the arena id
        ;; and that image-extension isn't overwritten when no file is present
        db-fields (merge
                   (select-keys record [:image-extension])
                   (dissoc input :_id :file)
                   (select-keys record [:arena-id]))]
    (if (image-uploaded? input)
      (merge db-fields (image-fields object-id (input->image-extension input)))
      db-fields)))

(defn create [input]
  (let [db-fields (create-input->db-fields input)]
    (db-insert db-fields)
    (when (image-uploaded? input) (store-images input db-fields))
    db-fields))

;; this is weird... i remove the object id in the ->db-fields method,
;; then add it back again
(defn update [input]
  (let [db-fields (update-input->db-fields input)
        object-id (ObjectId. (:_id input))
        record    (merge db-fields {:_id object-id})]
    (db-update-by-id object-id db-fields)
    (when (image-uploaded? input) (store-images input db-fields))
    db-fields))

;; TODO query S3 first to avoid missing any images if i.e. image
;; version names change
(defn- delete-images [record]
  (doseq [[vname] (conj *image-versions ["original"])]
    (s3/delete-object config/*aws-credentials* (bucket-name) (image-relative-path vname record))))

(defn destroy [_id]
  (let [object-id (ObjectId. _id)
        record (db-one-by-id object-id)]
    (delete-images record)
    (db-destroy object-id)))
```

`resize-and-save-image` has been broken into `input->images` and
`store-image`, lines 67 and 87, and neither has knowledge of the
other. I also introduced `create-input->db-fields` and
`update-input->db-fields`.

You might also have noticed some weird code at lines 15-18. The
functions being used are here:

```clojure
(ns arenaverse.data-mappers.db)

(defmacro add-db-reqs []
  '(do
     (require 'monger.collection)
     (import 'org.bson.types.ObjectId)))

;; TODO ~' Insanity! #cthulhu
;; These macros are meant to infect the namespace with functions. Why
;; would I want to do this? Should I take heed of the fact that
;; Clojure really doesn't want me to?

;; I wrote these fucntions to avoid having to write collection-name
;; all over the place
(defmacro add-db-fns [collection-name]
  `(let [collection-name# ~collection-name]
    (def ~'db-destroy (partial monger.collection/remove-by-id collection-name#))     
    (def ~'db-one (partial monger.collection/find-one-as-map collection-name#))
    (def ~'db-one-by-id (partial monger.collection/find-map-by-id collection-name#))
    (def ~'db-all (partial monger.collection/find-maps collection-name#))
    (def ~'db-insert (partial monger.collection/insert collection-name#))
    (def ~'db-update-by-id (partial monger.collection/update-by-id collection-name#))
    (def ~'db-update (partial monger.collection/update collection-name#))))

;; These methods are meant to generate the representations which
;; non-db parts of the code will use. They all convert ObjectId's to
;; strings because no other part of the system should care about ObjectId's
(defmacro add-finder-fns []
  '(do
     ;; TODO this doesn't feel like it belongs here. It's a helper
     ;; method. But this macro approach is infecting everything!
     (defn idstr [record]
       (.toString (:_id record)))
     
     (defn object-id->idstr [record]
       (assoc record :_id (idstr record)))

     ;; TODO I don't like mapping in the all fn, feels wasteful.
     (defn all [& [query-doc]]
       (map object-id->idstr (db-all query-doc)))
     
     (defn one [& [query-doc]]
       (if-let [r (db-one query-doc)]
         (object-id->idstr r)))
     
     (defn one-by-id [_id]
       (if-let [r (db-one-by-id (ObjectId. _id))]
         (object-id->idstr r)))))
```

Now this is some seriously weird code. I don't know if this is a good
idea at all - it doesn't feel like good Clojure or good lisp.

That being said, there are two macros, both of which create new
functions in the namespace in which they're called. The first set
simply wraps monger functions and keeps me from having to repeat the
mongodb collection name argument over and over. The second set of
functions performs the storage-to-domain-representation
transformations.

You can see in the comments that I feel seriously weirded out by this
code. It had me questioning what I'm really using namespaces for. If
`idstr` does the exact same thing in every namespace that it's copied
to, then why am I throwing it into multiple namespaces? `add-db-fns`
helps me reduce duplication, but is it really worth it? Perhaps
that'll be the subject of another blog post about another refactoring.

For completeness's sake, here's the refactored arena.clj:

```clojure
(ns arenaverse.data-mappers.arena
  (:require [arenaverse.data-mappers.db :as db]))

(db/add-db-reqs)
(let [collection-name "arenas"]
  (db/add-db-fns collection-name)
  (db/add-finder-fns))

(defn destroy [_id]
  (db-destroy (ObjectId. _id)))
```

## A Scarier Monster?

Some more doubts about the new code:

* Should the image processing code be somewhere else?
* Should I write a wrapper around the S3 code, as I did for the monger code? Should I be more explicit about the representation/persistence relationship?
* How about the `amazon-image-path` function? Where does that go? In this case, I actually am doing something beyond just mapping data.

## The End

Thus ends our exhausting - I mean, exhaustive - tour of this little refactoring. I'd love any feedback on the code!

Special thanks to ["Daniel Choi"](http://danielchoi.com/software/) for
linking to the Rich Hickey talk and to
["bostonrb"](http://bostonrb.org/) for a great discussion on
simplicity.
