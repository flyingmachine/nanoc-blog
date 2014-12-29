---
title: Dissecting The Dumbest Clojure/Noir App In Existence
created_at: Fri Aug 25 2012 13:23:00 -0500 2012
kind: article
categories: programming
summary: Recently, I released OMG! SMACKDOWN!!!, which is the dumbest Clojure/Noir app created to date. This post dissects it in excruciating detail.
additional_stylesheets:
  - pygments
---

Recently, I released [OMG! SMACKDOWN!!!](http://omgsmackdown.com),
which must be the dumbest Clojure/Noir app created to date. This post
dissects it in excruciating detail.

Here's a preview of what you're in for:

* App overview
* Data mappers
* Image resizing and S3 storage
* The (awesome!!!) battle page
* Account creation
* Authentication with friend
* Admining & Moderating
* Handling stylesheets
* Final thoughts

You can find all source code on
[github](https://github.com/flyingmachine/arenaverse. This article,
however, has a snapshot of the source, also on
[github](https://github.com/flyingmachine/nanoc-blog/tree/master/content/assets/source/clojure/omgsmackdown-dissected),
so that it will continue to make sense long after the app has evolved.

## App Overview

I created [OMG! SMACKDOWN!!!](http://omgsmackdown.com), a.k.a. the
dumbest Clojure/Noir app on the planet, over a period of three
months. Whereas other voting sites like "Hot or Not" and "Kitten War"
are focused on trivia, I aspired to build a site for voting on
humanity's more pressing questions. Questions like,
["Which deity is more badass?"](http://omgsmackdown.com/arenas/deity-bowling)
and
["Who loved his mama more?"](http://omgsmackdown.com/arenas/conquerors-57fd)
and
["Which creature is scarier?"](http://omgsmackdown.com/arenas/republicans-vs-monsters). The
kind of stuff that has made for many a sleepless night among
philosophers and stoners.

OMG! SMACKDOWN!!! lets you create an "arena" which you populate with "fighters". The fighters are shown, two at a time, along with the arena's question. Clicking on a fighter votes for that fighter. Part of the site's (undeniable) charm is that the fighter pairings are completely random, leading to delightful surprise after delightful surprise.

## Data Mappers

The app's data mappers are at the heart of everything. I've already
written a
[fairly thorough description](/programming/refactoring-to-datamappers-in-clojure/). Here
I'll focus on a briefer functional explanation so that you'll know
what's going on when you see data mapper code elsewhere.

OMGS!!! uses mongodb with access provided by [Clojure Monger](http://clojuremongodb.info/), an excellent mongodb library.

### Convenience Functions

One reason for writing the datamappers was to be able to write code like this:

```clojure
(defn filtered-arenas []
  (arena/all {:hidden {$exists false}}))
```

As you can see, the database details are completely hidden. View code
shouldn't have to worry about storage details. Also, I find this code
pretty readable - it should be pretty obvious what's going on
here. The alternative would be something like:

```
(defn filtered-arenas []
  (mc/find-maps "arena" {:hidden {$exists false}}))
```

Not a huge difference, but the former is definitely better.

In order to achieve this convenience, though, I had to do some dark
voodoo. This kind of stuff will probably taint your soul:

```clojure
(ns arenaverse.data-mappers.db)

(defmacro add-db-reqs []
  '(do
     (require 'monger.collection)
     (import '(org.bson.types ObjectId))
     (use 'monger.operators)))

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
         (object-id->idstr r)))

     (defn unset [_id field]
       (db-update-by-id (ObjectId. _id) {$unset {field 1}}))))
```

As you can see, those macros "infect" a namespace when they're
called. For example, I call them here in the
`arenaverse.data-mappers.arena` namespace:

```clojure
(db/add-db-reqs)
(let [collection-name "arenas"]
  (db/add-db-fns collection-name)
  (db/add-finder-fns))
```

These macro calls a) ensure dependencies are met and b) expand to a
series of function definitions. It feels really wrong to use macros to
create nearly-identical functions in one namespace after another, but
I'm not sure what a better alternative would be.

### Conventions

Another function served by these macros is that they ensure that
conventions are followed. For example, I wanted all queries to convert
a MongDB `ObjectId` object to a simple string. This was another way of
hiding an implementation detail - views shouldn't know WTF an
`ObjectId` is. You can see that the dark voodoo macros help in this
regard:

```clojure
     (defn object-id->idstr [record]
       (assoc record :_id (idstr record)))

     ;; TODO I don't like mapping in the all fn, feels wasteful.
     (defn all [& [query-doc]]
       (map object-id->idstr (db-all query-doc)))
     
     (defn one [& [query-doc]]
       (if-let [r (db-one query-doc)]
         (object-id->idstr r)))
```

One flaw with this approach is that it doesn't quite cover every
situation. For example, when I create a user I have to manually
convert the `ObjectId` to string:

source. clojure/omgsmackdown-dissected/data_mappers/user.clj 21-24

This is necessary in order to remain consistent with the convention
that data mapper functions convert the `_id` field to a string. In
most cases, this doesn't matter because I don't actually do anything
with the return value but in this case it mattered because of the way
that user signup works. Here's what would happen if I didn't manually
convert the `_id`:

* User signs up
* Session is populated with user details, including an `ObjectId` for the `_id` field
* User creates arena. `user-id` field of arena is of type `ObjectId`
* User logs out
* User signs in again. Session is populated with user details, including a `String` for the `_id` field (because the query methods from db.clj do this conversion)
* List of user's arenas doesn't include the arena created earlier because the query is looking for the `String` version of the user's `_id` instead of the `ObjectId` version

Coming from a Rails background, I'm used to libraries like mongoid
which handle this conversion/consistency issue for you. My solution is
a little half-baked and it requires me to pay attention to details
which I'm not used to paying attention to.

## Image resizing and S3 storage

OMGS!!! wouldn't be half as fun if images weren't involved. I was too
lazy to try and find a library that would handle resizing an image,
associating it with a record, and storing it wherever I wanted (there
are probably five thousand such gems for Rails). Such a library would
probably completely unworkable anyway since my datamapper coder is
completely custom. It's all throughout the code below:

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
                      ["battle" 352 638]])

(defn image-relative-path [version {:keys [_id image-extension]}]
  (str "fighters/" _id "/" version "." image-extension))

(defn image-path [version record]
  (str "/" (image-relative-path version record)))

;; TODO not hardcode this
(defn- bucket-name []
  (str "arenaverse-" (name config/env)))

;; This is used when displaying an image
(defn amazon-image-path [version record]
  (str "https://s3.amazonaws.com/" (bucket-name) (image-path version record)))

(defn- normalize-image-extension [extension]
  (clojure.string/replace extension "jpeg" "jpg"))

(defn- image-fields [image-extension]
  {:image-extension (and image-extension (normalize-image-extension image-extension))})

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

;; TODO paul graham says this is crappy code - but is it easier to understand?
(defn- input->images [input]
  (if (:file input)
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
            original-image))))

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

(defn- input->team [input]
  (let [new-team (:new-team input)]
    (if (or (nil? new-team) (= "" new-team))
      (:team input)
      new-team)))

;; TODO refactor the common dissoc merge pattern
(defn- create-input->db-fields [input]
  (let [object-id (ObjectId.)
        team (input->team input)]
    (merge
     (dissoc input :file :team :new-team)
     {:_id object-id
      :team team}
     (image-fields (input->image-extension input)))))

(defn- update-input->db-fields [input]
  (let [team (input->team input)
        db-fields (merge (dissoc input :file :team :new-team) {:team team})]
    (if (image-uploaded? input)
      (merge db-fields (image-fields (input->image-extension input)))
      db-fields)))

(defn create [input]
  (let [db-fields (create-input->db-fields input)]
    (db-insert db-fields)
    (when (image-uploaded? input) (store-images input db-fields))
    db-fields))

;; this is weird... i remove the object id in the ->db-fields method,
;; then add it back again
(defn update [_id input]
  (let [db-fields (update-input->db-fields input)]
    (println db-fields)
    (db-update-by-id (ObjectId. _id) {:$set db-fields})
    (let [record (one-by-id _id)]
      (when (image-uploaded? input) (store-images input record))
      record)))

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

As you can see, image-processing code takes up a good 2/3 of all the
code, which is probably a sign that those functions belongs in their
own namespace. I'll probably do that someday, but for now the code is
only being used by the `fighter` model so I feel OK leaving it where
it is.

The code is a bit hard to decipher, but in general I tried to continue
following the approach of clearly separating a) data b) transformation
functions and c) storage functions. The form input is converted to a
sequence of java Buffered Images, which are then resized, which are
then transformed to input streams, which are then stored in S3. When
updating a fighter I had to take particular care to preserve the
`image-extension` field when the fighter's form was submitted without
an image.

There's also something weird happening in the update function:

```clojure
(defn update [_id input]
  (let [db-fields (update-input->db-fields input)]
    (println db-fields)
    (db-update-by-id (ObjectId. _id) {:$set db-fields})
    (let [record (one-by-id _id)]
      (when (image-uploaded? input) (store-images input record))
      record)))
```

I update the record, then immediately retrieve it from mongodb in
order to possibly update the image and to return the fighter
record. It feels strange to have to perform a `select` during an
update.

## The (awesome!!!) battle page

The battle page is where all the magic happens! It's where you're
presented with all of life's deepest questions, ready to be voted on
with a simple mouse click. Here's the full code, which will be
analyzed in more detail below:

```clojure
(ns arenaverse.views.battles
  (:require [arenaverse.views.common :as common]
            [arenaverse.views.admin.fighters :as fighters]
            [arenaverse.data-mappers.fighter :as fighter]
            [arenaverse.data-mappers.arena :as arena]
            [arenaverse.data-mappers.battle :as battle]
            [noir.session :as session])
  
  (:use noir.core
        hiccup.core
        hiccup.form-helpers
        arenaverse.views.routes
        monger.operators)
  
  (:import [org.bson.types ObjectId]))


;;------
;; Functions for setting up data for displaying a battle page
;;------
;; TODO how to test this?

;; When there are no teams, it's everybody against everybody
(defn random-teamless-fighters [fighters]
  (let [randomer #(rand-int (count fighters))
        left (randomer)
        right (first (filter #(not= % left) (repeatedly randomer)))]
    [(nth fighters left) (nth fighters right)]))

;; When we're dealing with an arena that has teams, we need to ensure
;; that we don't pit two fighters from the same team against each other
(defn random-team-fighters [fighters]
  (let [randomer #(nth fighters (rand-int (count fighters)))
        left (randomer)
        right (first (filter #(not= (:team %) (:team left)) (repeatedly randomer)))]
    [left right]))

;; Return a random list of fighters for a given arena id
(defn random-fighters [arena-id]
  ;; The image-extension filter ensures that we don't get fighters
  ;; that are missing an image
  (let [fighters (fighter/all {:arena-id arena-id
                               :hidden {$exists false}
                               :image-extension {$exists true $ne ""}})]
    (if (> (count fighters) 1)
      (if (some #(not (empty? (:team %))) fighters)
        (random-team-fighters fighters)
        (random-teamless-fighters fighters))
      [])))

;; Convert a battle record into the format we want to store in the session
(defn battle->session-battle [battle]
  (let [shortname (:shortname (:arena battle))]
    (conj (map :_id (:fighters battle)) shortname)))

;; Takes a seq of battles, which are a map of arena and two fighters
;; for that arena. Save all battles in the session so that we know who
;; the loser was when the user selects a winner.
(defn register-battles! [b]
  (let [battles-processed (apply hash-map
                                 (reduce (fn [list battle]
                                           (let [bs (battle->session-battle battle)]
                                             (conj list (first bs) bs)))
                                         []
                                         b))]
    (session/put! :battles battles-processed)
    (session/put! :main-battle (battle->session-battle (first b)))))

(defn arena->battle [arena]
  {:arena arena :fighters (random-fighters (arena/idstr arena))})

;; Ensure that we only deal with displayable battles
(defn battle-filter [battles]
  (filter #(>= (count (:fighters %)) 2) battles))

;; Arenas can be hidden through moderation
(defn filtered-arenas []
  (arena/all {:hidden {$exists false}}))

;; If the main arena isn't specified we don't have to do anything
;; special to ensure the order of the battles returned
(defn battles-without-main-arena-specified []
  (shuffle (battle-filter (map arena->battle (filtered-arenas)))))

;; When the main arena is specified, then the battle in that arena
;; needs to be at the head of the seq returned. This is because the
;; battle partial designates the first battle as the "main" one
(defn battles-with-main-arena-specified [main-arena]
  (let [arenas (remove #(= main-arena %) (filtered-arenas))]
    (reverse (conj (shuffle (battle-filter (map arena->battle arenas))) (arena->battle main-arena)))))

;; This just calls one of the above two functions as appropriate and
;; then registers the battles in the session
(defn battles [main-arena]
  (let [b (if main-arena
            (battles-with-main-arena-specified main-arena)
            (battles-without-main-arena-specified))]
    (register-battles! b)
    b))

;;----------
;; Partials for battle page
;;----------
(defpartial card [arena record img-version]
  [:div.name (:name record)]
  [:div.pic
   [:a {:href (url-for-r :battles/winner {:_id (:_id record) :arena-shortname (:shortname arena)})}
    (fighters/fighter-img img-version record)]])

;; I don't even remember what this is for
(defpartial card-without-battle [record img-version]
  [:div.name (:name record)]
  [:div.pic
   (fighters/fighter-img img-version record)])

(defpartial win-ratio [fighter wins]
  (let [bouts (reduce + (vals wins))
        _id (keyword (:_id fighter))
        ratio (* 100 (if (= 0 bouts) 1 (/ (_id wins) bouts)))]
    [:div.ratio-card
     (card-without-battle  fighter "card")
     [:div.win-ratio (str (format "%.1f" (double ratio)) "%")]]))

;; Minor battles are all the battles displayed after the "main" one at
;; the top
(defpartial _minor-battle [battle]
  (when battle
    (let [[left-f right-f] (:fighters battle)
          arena (:arena battle)]
      [:div.battle
       [:h2 (:fight-text (:arena battle))]
       [:div.fighter.a (card arena left-f "card")]
       [:div.fighter.b (card arena right-f "card")]])))

;; Divide the minor battles into rows so that they show up correctly
(defpartial _minor-battle-row [row]
  [:div.row
   (map _minor-battle row)])

(defpartial _minor-battles [minor-battles]
  (let [rows (partition 2 2 [nil] minor-battles)]
    [:div#minor-battles
     (map _minor-battle-row rows)]))

(defpartial previous-battle-results [prev-fighter-id-a prev-fighter-id-b]
  (when (and prev-fighter-id-a prev-fighter-id-b)
    (let [previous-fighters (map #(fighter/one-by-id %) [prev-fighter-id-a prev-fighter-id-b])
          wins (battle/record-for-pair (map :_id previous-fighters))]
      [:div.win-ratios
       [:h2 "Win Ratio"]
       (win-ratio (first previous-fighters) wins)
       (win-ratio (second previous-fighters) wins)])))

;; This will display the main arena. Maybe it should be named main-arena
(defpartial main-area [arena left-f right-f]
  [:div#battle
   [:div.fighter.a
    (when left-f (card arena left-f "battle"))]
   [:div.fighter.b
    (when right-f (card arena right-f "battle"))]])

(defpartial battle [{:keys [prev-fighter-id-a
                            prev-fighter-id-b
                            prev-main-arena-shortname
                            main-arena-shortname]}]
  (let [designated-main-battle (when main-arena-shortname (arena/one {:shortname main-arena-shortname}))
        [main-battle & minor-battles] (battles designated-main-battle)]
    (when main-battle
      (let [[left-f right-f] (:fighters main-battle)
            arena (:arena main-battle)]
        (common/layout 
         [:h1 (:fight-text (:arena main-battle))]
         [:div#battles
          (main-area arena left-f right-f)
          (_minor-battles minor-battles)]
         [:div#secondary
          (previous-battle-results prev-fighter-id-a prev-fighter-id-b)])))))

;; This is used to convert the data stored in a session for a battle
;; into something usable by the battle partial
(defn session-battle->battle-map [session-battle]
  (let [[prev-main-arena-shortname prev-fighter-id-a prev-fighter-id-b] session-battle]
    {:prev-main-arena-shortname prev-main-arena-shortname
     :prev-fighter-id-a prev-fighter-id-a
     :prev-fighter-id-b prev-fighter-id-b}))

;; The home page. Show completely random battles
(defpage-r listing []
  (battle (session-battle->battle-map (session/get :main-battle))))

;; When a user clicks on an image, determine which battle it's from so
;; that you can record the winner and show that battle's arena in the
;; main area
(defpage-r winner {:keys [arena-shortname _id]}
  (let [selected-battle ((session/get :battles) arena-shortname)
        selected-battle-fighter-ids (rest selected-battle)]
    (battle/record-winner! selected-battle-fighter-ids _id)
    (let [battle-map (session-battle->battle-map (or selected-battle (session/get :main-battle)))]
      (battle (assoc battle-map :main-arena-shortname (:prev-main-arena-shortname battle-map))))))

;; When you want to show a specific arena
(defpage-r arena {:keys [shortname]}
  (battle {:main-arena-shortname shortname}))
```

### Choosing the "main" arena

There are three different ways in which the "main" arena - the large one at the top - is chosen:

* You're viewing the home page, "/". The main arena should be completely random.
* You've just clicked an image (any image) as the winner of an
  arena. The main arena should be the one which the fighter you
  clicked on belongs to.
* You're viewing an arena directly, "/arenas/arena-name". Example:
  [Which creature is scarier?](http://omgsmackdown.com/arenas/republicans-vs-monsters). This
  is so that users can directly share an arena they've created or
  like.

Additionally, whenever the page is refreshed it's necessary to show
the winner of the previous main arena. All of this is accomplished by
defining a partial which takes the main arena and previous fighters as
parameters, along with page definitions which send the required info:

```clojure
(defpartial battle [{:keys [prev-fighter-id-a
                            prev-fighter-id-b
                            prev-main-arena-shortname
                            main-arena-shortname]}]
  (let [designated-main-battle (when main-arena-shortname (arena/one {:shortname main-arena-shortname}))
        [main-battle & minor-battles] (battles designated-main-battle)]
    (when main-battle
      (let [[left-f right-f] (:fighters main-battle)
            arena (:arena main-battle)]
        (common/layout 
         [:h1 (:fight-text (:arena main-battle))]
         [:div#battles
          (main-area arena left-f right-f)
          (_minor-battles minor-battles)]
         [:div#secondary
          (previous-battle-results prev-fighter-id-a prev-fighter-id-b)])))))

;; This is used to convert the data stored in a session for a battle
;; into something usable by the battle partial
(defn session-battle->battle-map [session-battle]
  (let [[prev-main-arena-shortname prev-fighter-id-a prev-fighter-id-b] session-battle]
    {:prev-main-arena-shortname prev-main-arena-shortname
     :prev-fighter-id-a prev-fighter-id-a
     :prev-fighter-id-b prev-fighter-id-b}))

;; The home page. Show completely random battles
(defpage-r listing []
  (battle (session-battle->battle-map (session/get :main-battle))))

;; When a user clicks on an image, determine which battle it's from so
;; that you can record the winner and show that battle's arena in the
;; main area
(defpage-r winner {:keys [arena-shortname _id]}
  (let [selected-battle ((session/get :battles) arena-shortname)
        selected-battle-fighter-ids (rest selected-battle)]
    (battle/record-winner! selected-battle-fighter-ids _id)
    (let [battle-map (session-battle->battle-map (or selected-battle (session/get :main-battle)))]
      (battle (assoc battle-map :main-arena-shortname (:prev-main-arena-shortname battle-map))))))

;; When you want to show a specific arena
(defpage-r arena {:keys [shortname]}
  (battle {:main-arena-shortname shortname}))
```

### Randomizing arena order

Arena randomization is handled with these functions:

```clojure
;; Ensure that we only deal with displayable battles
(defn battle-filter [battles]
  (filter #(>= (count (:fighters %)) 2) battles))

;; Arenas can be hidden through moderation
(defn filtered-arenas []
  (arena/all {:hidden {$exists false}}))

;; If the main arena isn't specified we don't have to do anything
;; special to ensure the order of the battles returned
(defn battles-without-main-arena-specified []
  (shuffle (battle-filter (map arena->battle (filtered-arenas)))))

;; When the main arena is specified, then the battle in that arena
;; needs to be at the head of the seq returned. This is because the
;; battle partial designates the first battle as the "main" one
(defn battles-with-main-arena-specified [main-arena]
  (let [arenas (remove #(= main-arena %) (filtered-arenas))]
    (reverse (conj (shuffle (battle-filter (map arena->battle arenas))) (arena->battle main-arena)))))

;; This just calls one of the above two functions as appropriate and
;; then registers the battles in the session
(defn battles [main-arena]
  (let [b (if main-arena
            (battles-with-main-arena-specified main-arena)
            (battles-without-main-arena-specified))]
    (register-battles! b)
    b))
```

The `battles-with-main-arena-specified` function looks kind of goofy to me. It's weird to remove the main arena, shuffle the rest, then add the main arena back in, but maybe that's fine.

### Randomizing Fighters

```clojure
;; When there are no teams, it's everybody against everybody
(defn random-teamless-fighters [fighters]
  (let [randomer #(rand-int (count fighters))
        left (randomer)
        right (first (filter #(not= % left) (repeatedly randomer)))]
    [(nth fighters left) (nth fighters right)]))

;; When we're dealing with an arena that has teams, we need to ensure
;; that we don't pit two fighters from the same team against each other
(defn random-team-fighters [fighters]
  (let [randomer #(nth fighters (rand-int (count fighters)))
        left (randomer)
        right (first (filter #(not= (:team %) (:team left)) (repeatedly randomer)))]
    [left right]))
```

## Account Creation

Holy crap, this article is so freaking long. Anyway, noir's validation
helpers really helped out when I wrote account creation:

```clojure
(ns arenaverse.views.users
  (:require [arenaverse.views.common :as common]
            [arenaverse.data-mappers.user :as user]
            [arenaverse.models.permissions :as can]
            [noir.session :as session]
            [noir.response :as res]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows]
            [noir.validation :as vali])
  
  (:use noir.core
        hiccup.core
        hiccup.form-helpers
        arenaverse.views.routes))

(defn valid? [{:keys [username password]}]
  (vali/rule (vali/min-length? username 4)
             [:username "Your username must be at least 4 characters"])
  (vali/rule (nil? (user/one {:username username}))
             [:username "That username is already taken :("])
  (vali/rule (vali/min-length? password 4)
             [:password "Your password must be at least 4 characters"])
  (not (vali/errors? :username :password)))

(defpartial error-item [[first-error]]
  [:p.error first-error])

(defpage-r shiny {:as user}
  (common/layout
   [:h1 "Sign Up!"]
   [:p "Wow, you are about to make one of the best decisions of your life. Congratulations!"]
   (form-to [:post (url-for-r :users/create)]
            [:div
             [:div.control-group
              (vali/on-error :username error-item)
              (label "username" "Username")
              [:span.help "Must be at least 4 characters"]
              [:div.controls (text-field "username" (:username user))]]
             [:div.control-group
              (vali/on-error :password error-item)
              (label "password" "Password")
              [:span.help "Must be at least 4 characters"]
              [:div.controls (password-field "password")]]
             [:div.form-controls (submit-button "Sign Up")]])))

(defn register [{:keys [uri request-method params]}]
  (when (and (= uri "/users")
             (= request-method :post))
    (if (valid? params)
      (workflows/make-auth (user/create params)))))

(defpage-r create {:as user}
  (render users-shiny user))
```

All that `valid?` and `vali/on-error` stuff is pretty much straight
from the tutorial. So much so that I actually have that `error-item`
partial copied and pasted all over my code, which is kind of crappy
but which is also something that's easily fixable.

You may be wondering what the `register` function is doing there. I'll
get into that in the next section.

## Authentication with friend

[cemerick's friend](https://github.com/cemerick/friend) library seems
to be fairly popular. I found it a little confusing to work with so
hopefully these notes help other Clojurists making other, less
ridiculous web sites.

### Logging in automatically after creating an account

In order to log a user in automatically after creating an account, I
had to create a custom workflow. Here's the friend code:

```clojure
(server/add-middleware
 friend/authenticate 
 {:credential-fn (partial creds/bcrypt-credential-fn credential-fn)
  :workflows [(workflows/interactive-form), arenaverse.views.users/register, session-store-authorize]
  :login-uri "/login"
  :unauthorized-redirect-uri "/login" 
  :default-landing-uri "/admin"})
```

`arenaverse.views.users/register` handles the login, which made this
weird because had to require view functions within my `server.clj`
file. This felt pretty wrong. Anyway, here's the register function:

```clojure
(defn register [{:keys [uri request-method params]}]
  (when (and (= uri "/users")
             (= request-method :post))
    (if (valid? params)
      (workflows/make-auth (user/create params)))))
```

Looking at this now, it's hard to even reason out what's going on and
how it relates to the friend library. I think what's happening though
is that this function is called by the friend
"middleware":https://github.com/ring-clojure/ring on every single
request. It's therefore necessary to specify that its logic should
only be run when the given criteria are met.

When the criteria are met - when the user posts to `/users` - then the
function checks to see if the params are valid. If they are then
friend's `workflows/make-auth` function is called with the result of
`user/create` (remember how I elaborated on that above?). This
function does some session magic or something. From then on friend
considers you logged in.

If the params are invalid, then nil is returned and your request gets
processed like it normally would. This means that the `create` page
gets called, which in turn renders `shiny`. Since your params are
invalid, error messages will show up.

### Remaining logged in when the server restarts

Another issue with friend is that your session data isn't stored in a
cookie. Therefore, every time heroku spins the app down you get logged
out. This is problematic because there's no password recovery
functionality and also because it's just lame.

To resolve this I did the following:

1. Use clojure-monger's session store function to persist session info to mongodb:

    ```clojure
      (:use [monger.ring.session-store :only (session-store)]))
    
    (server/load-views "src/arenaverse/views/")
    
    (defn -main [& m]
      (let [mode (keyword (or (first m) :dev))
            port (Integer. (get (System/getenv) "PORT" "8080"))]
        (let [uri (get (System/getenv) "MONGOLAB_URI" (str "mongodb://127.0.0.1/omgsmackdown-" (name mode)))]
          (monger.core/connect-via-uri! uri))
        (server/start port {:mode mode
                            :ns 'arenaverse
                            :session-store (session-store)})))
    ```

2. Create a session-store-authorize workflow function

```clojure
    (defn session-store-authorize [{:keys [uri request-method params session]}]
      (when (nil? (:cemerick.friend/identity session))
        (if-let [username (noir-session/get :username)]
          (workflows/make-auth (user/one {:username username})))))
```

3. Add the workflow when adding the `friend/authenticate` middleware, which you can see above.

### Overall Friend Confusion

I have to admit that I don't fully get sessions/cookies/auth in
Clojure. With friend & noir, I think the difficulty lies in the fact
that friend is meant to work directly with ring, whereas noir provides
a little magic on top of ring.

If I understand correctly, ring sessions are perpetuated in a kind of
repeating reduce, where the session data for one request is sent to a
function. The function then transforms the data, and that's used for
the next request. I don't know if I'm explaining it well but I think
the key thing is that the session data is never requested and state is
never modified.

Noir, on the other hand, somehow stores session data by modifying
state. So noir's session and the session variables created by friend
exist in two totally different places.

The other way in which noir's "magic" can cause confusion is that, by
default, you don't have to care about ring and middlewares. So when
you do have to care about middleware, like with ring, it's hard to
visualize where it's coming into play and how it's interacting with
your other routes.


## Admining & Moderating

OK this article really is too long now. Check out `models/permissions`
and everything under `views/admin` and `views/moderate` to see what's
going on.

## Handling Stylesheets

You can see my source files in `resources/compass` in the git
repo. Basically, I ran `compass watch` while doing
development. Compass, Sass, and Susy are all great libraries for
making good-looking sites. I made the logo with photoshop.

## Final Thoughts

This was my first web site using Clojure and Noir. I've been using Rails for almost 7 years now, and there was a lot for me to get used to. Here's some stuff I liked:

* Clojure is awesome. It's a powerful lisp, and having access to Java makes it that much more powerful. Java was used for:
    * Processing images
    * Uploading to S3
    * Interacting with MongoDB
* I really enjoyed being so close to the "metal"
    * Creating my own data-mapper abomination thing was quite fun
    * The lack of magic felt freeing. I felt like I was only using what I needed, and that made my life a lot easier. I really don't know how else to describe that.
* It was fun to make something so ridiculous.

The only major drawback is the lack of libraries compared to
Rails. For the time being, I'll continue reaching for Rails for
professional work but I'll definitely use Clojure for my own side
projects.

Wow, so that's it! I hope you liked this detailed look at the silliest
Clojure/Noir web site ever made! And if you like the site, please feel
free to copy and it and do whatever you want with it. It'd be awesome
to hear about it being installed on intranets with battles like "Which
employee would win a drinking contest?"
