---
title: "Deeper Into the Land of Lisp: First Contact with Noir"
created_at: Jun 25 19:30:00 -0500 2012
kind: article
categories: programming
summary: "Clojure and noir are great fun. This post describes some of the code I wrote and gives some tips and tricks. There's other stuff too but I'm not going to try and summarize it here."
additional_stylesheets:
  - pygments
---

Two days ago I got my
[first noir app running](http://omgsmackdown.com) on Heroku. This post
is a mishmash of tips, tricks, processes and opinions on whole
experience.

## What's noir?

[noir](http://webnoir.org/) is a really freakin lightweight web
framework for [Clojure](http://clojure.org/) written by
[@ibdknox](https://twitter.com/#!/ibdknox), a.k.a. the dude behind the
[Light Table IDE](http://www.chris-granger.com/2012/06/24/its-playtime/)
that everyone's going nuts about.

## I'm not joking, it's really freakin lightweight

I came to noir from Rails, and holy crap noir is tiny by
comparison. So far, this is a really good thing because it's made it
easy for me to learn. It's actually kind of scary because I've been
using Rails for almost seven years and I might know noir better than I
know Rails. It's like Rails is the teenage kid that has its own slang
and way of dressing and a lot of it is incomprehensible to me and I
end up looking like some terribly uncool fuddy duddy who uses terms
like "fuddy duddy", and I'm only 27 for the love of Martha.

How do I create a Rails template again? Do I have to do anything
special to get an engine to use the assets pipeline? For me, Rails
development is often a test of my Googling skills. These are the kinds
of questions I don't have to ask when using noir, because the answer
is always "take your fancy flimdoodles and get the hell off my yard."
Actually, the answer is more like "uh, I guess you could build that
yourself if you really wanted to."

Either way, I really appreciate how bare-bones noir is. But who knows,
maybe in a month or two I'll be trolling the clj-noir mailing list
insisting that they add jquery integration.

@@ Code stuff I did

Here's a list of random things I did in my project:

### Created a config file

I created a config file to handle global variables. 

```clojure
(ns arenaverse.config)

(def ^:dynamic *aws-credentials* {:secret-key (System/getenv "AWS_SECRET")
                                  :access-key "SECRET!"})

(def env (get (System/getenv) "SERVER_ENV" "dev"))
```

To get `SERVER_ENV` into my app on heroku, my Procfile reads as follows:

`web: SERVER_ENV=production lein trampoline run -m arenaverse.server`

The vars were used a few places. For example, `env` was used to
generate the name of the S3 bucket to store images in. I didn't want
my prod and dev images mixing.

### Put together some code for resizing images and uploading to S3

Below is the real meat of the application. Most if deals with resizing and uploading images. On lines 82 and 101 I use futures, which is pretty fun. Since I'm the only using the admin interface right now, it doesn't matter if an image isn't immediately available. Using futures allowed me to add the "fighters" more quickly. Later on, I might move thumbnail generation and uploading so it happens in the current thread but keep all the other image sizes in a future.

Right now it's all still pretty messy but one day I'll clean it up. Yeah.

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

(def *image-versions [["card" 192]
                      ["listing" 64]
                      ["battle" 432 638]])

(def *collection "fighters")

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

(defn delete-images [record]
  (doseq [[vname] (conj *image-versions ["original"])]
    (s3/delete-object config/*aws-credentials* (bucket-name) (image-relative-path vname record))))

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

### Eyepatches

In the above code I have the variable `*image-versions`. I wanted to
designate that this is a global variable, but clojure yelled at me
when I put on earmuffs (`*image-versions*`). So I'm calling this
one-asterisk approach an eyepatch, and I sincerely hope it catches on.

h3. Avoiding cyclic load dependency errors when generating URL's

I wrote about this in
[my last post](http://www.flyingmachinestudios.com/programming/global-noir-routes/).

h2. How I did the visual part

For the CSS, I used [compass](http://compass-style.org/) and
[susy](http://susy.oddbird.net/). I know twitter bootstrap is super
cool right now, and with good reason, but Susy and Compass are great
if you want a grid and a specific look and don't want to undo a ton of
bootstrap styles. It's more lightweight. Just like noir! Compass also
has great vertical rhythm helpers, which bootstrap doesn't.

For the top banner, I initiall used
[Sketch](http://bohemiancoding.com/sketch/). This was my first time
using Sketch and it was pretty fun to use. There was only one really
annoying thing about it (the artboard that wouldn't disappear no
matter what), and when the need arises I'd like to try it again.

Ultimately I went with Photoshop, because that's what I'm used to. I
was able to find a set of paint splatter brushes really quickly which
let me create the look I wanted. I also used photoshop for the
background of two main images on the [home
page](http://omgsmackdown.com/).

The background I picked up from "Subtle
Patterns":http://subtlepatterns.com/, one of my top 5 design
resources.

h2. Things I want to do

* Testing - this looks like it will be easy but I kept barrelling forward because I wanted to get something up where everyone could see it. Because my site totally needs to be seen by everyone.
* Persistence vs business logic - I'm not really happy with having database interaction code in the same file as more business logic stuff. Besides that, the db interaction code is largely the same from one model to the next.
* Move the image processing - this stuff probably belongs in its own library, or at least in a utility file

h2. Never a "d'oh!" moment... Well, maybe a few

For the most part, I didn't get hung up on issues that took hours and hours of head scratching to solve. But here are a few things that tripped me up:

* Cyclic load dependency - mentioned above
* Macro evaluation - I still sometimes get tripped up when I expect macro arguments to get evaluated as soon as they're read rather than passed into the macro body somehow. This happened a little bit when writing the url-for-r code
* AWS bucket issue - I asked a "stupid question":https://groups.google.com/d/msg/clojure-mongodb/WiXQ57MIPNM/K7cLlO-UI50J on the "monger":http://clojuremongodb.info/ mailing list. I was passing a variable into a monger function and got totally mixed up about what the value was. It was a bit difficult to determine the actual value. There are some details under Tips & Tricks, below, on how I figured it out

h2. Compared to Common Lisp

I can't help but compare my experience with Clojure and noir to my experience with CL. Overall, Clojure has been much more enjoyable, for the following reasons:

h3. People actually use it

There are a lot more resources out there on doing practical things with it. I still don't know what I'd use CL for.

h3. You have java libraries at your disposal

Yep, this really makes a differnce. The AWS and mongodb libraries I used rely on Java libraries, and I used a Java image scaling library directly. It's great to have so much code already written for you.

Whereas in common lisp there weren't nearly as many libraries available, and finding them was a chore. One of the reasons I started using Clojure was because the JSON library I was using wouldn't encode lists nested four levels deep, even though the documentation indicated that that should work. And it worked with lists nested three levels deep just fine.

h3. CL packaging vs. Leiningen

I really don't get Common Lisp packaging. ASDF, quicklisp - I've used them and they work, I think, but it all felt so clunky and incomplete to me, especially coming from Ruby where Bundler and rubygems.org do a great job. CL packaging just feels hacked together. As far as I can tell, you can't even specify the versions of your dependencies. For one of my dependencies, quicklisp pulled in something that was two years old.

I'm not trying to disparage quicklisp here. It's just that I don't want to deal with those kinds of problems. I want to make stuff. I don't want to get bogged down in figuring out these byzantine, poorly-documented issues.

h3. Syntax

I think anyone who tries clojure will agree that the syntax is  superior. At first I was skeptical of the lack of parentheses, but now I can see that that's definitely a big win. Being able to quickly tell, visually, what kind of data structure you're working with is awesome.

h2. Tips & Tricks

* Cemerick's [clojure book](http://amzn.to/1MyXhCx) is the best, hands down. Just get it already.
* Use IRC - people on #clojure are super freakin helpful
* People are really great on Twitter, too. I randomly mentioned a couple issues I had, and it was like these magical clojure faeries came and solved my problem for me. Pretty great.
* Stack Overflow and reddit are good too.
* When working with noir, you can get a repl by doing `lein repl` and then starting your app, probably with `(-main)`. This was very helpful.
* You can edit jar files directly in emacs if you C-x f whatever.jar
* You can also tell leiningen to unpack your jar files, though I don't remember how this is done. I'm sure it's in the documentation somewhere
* Don't be afraid to just mess around. If you're coming from Rails like me the lack of structure might feel unsettling. One way to get over this is to just mess around and enjoy your newfound freedom.

## Thank Yous

Thanks to the following people for helping me:

* @trptcolin
* @michaelklishin
* @alandipert
* @technomancy
* @seancorfield

## What's Next

The next step for OMG! SMACKDOWN!!! is to allow users to create their own "arenas". After that... who knows? Maybe it'll be time to finally bring my game "Hobbit vs. Giant" to life :)
