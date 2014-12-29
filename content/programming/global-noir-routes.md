---
title: Global Noir Routes
created_at: Jun 25 8:30:00 -0500 2012
kind: article
categories: programming
summary: "Noir has a helper function, url-for, which you can use to generate URL's when given a named route. The problem arises when you have two different views which need to have links to each other."
additional_stylesheets:
  - pygments
---
"[^"]*":[^ *]

## The Problem

Noir has a
["helper function"](http://webnoir.org/autodoc/1.2.1/noir.core-api.html#noir.core/url-for),
`url-for`, which you can use to generate URL's when given a named
route. The problem arises when you have two different views which need
to have links to each other:

```clojure
;;----------
;; src/url_for_alternative/views/view_a.clj
;;----------
(ns url-for-alternative.views.view-a
  (:require [url-for-alternative.views.common :as common]
            [url-for-alternative.views.view-b :as view-b])  
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage list "/view-a" []
         (common/layout
           [:p "This is View A"]
           [:a {:href (url-for view-b/list)} "Go to View B"]))

;;----------
;; src/url_for_alternative/views/view_b.clj
;;----------
(ns url-for-alternative.views.view-b
  (:require [url-for-alternative.views.common :as common]
            [url-for-alternative.views.view-a :as view-a])
  (:use [noir.core :only [defpage]]
        [hiccup.core :only [html]]))

(defpage list "/view-b" []
         (common/layout
           [:p "This is View B"]
           [:a {:href (url-for view-a/list)} "Go to View A"]))
```

If you try to start your server with the above code in place you'll
get a `Cyclic load dependency` exception. View A requires View B,
which requires View A, which requires View B, etc. etc. etc. ibdknox
["mentioned this problem"](https://groups.google.com/d/msg/clj-noir/QTc_dlJiLWQ/FiDEcCeFVIcJ)
recently on the noir mailing list.

Here's some code I threw together to address the problem, along with
two basic examples of the code being used:

```clojure
;;----------
;; src/url_for_alternative/views/routes.clj
;;----------
(ns url-for-alternative.views.routes
  (require [clojure.string :as string]))

;; this is taken from noir
(defn- throwf [msg & args]
  (throw (Exception. (apply format msg args))))

(def routes '{:view-a/listing         "/view-a"
              :view-b/listing         "/view-b"})

(defn url-for-r
  ([route-name] (url-for-r route-name {}))
  ([route-name route-args]     
     (let [entry (route-name routes)
           route  (or (first (filter string? (flatten entry))) entry)
           route-arg-names (noir.core/route-arguments route)]
       (when (nil? route)
         (throwf "missing route for %s" route-name))
       (when (not (every? #(contains? route-args %) route-arg-names))
         (throwf "missing route-arg for %s" [route-args route-arg-names]))
       (reduce (fn [path [k v]]
                 (assert (keyword? k))
                 (string/replace path (str k) (str v))) route route-args))))

(defn- view-ns [namespace]
  ((re-find #"views\.(.*)$" (str (ns-name namespace))) 1))

(defn- dashed [namespace]
  (string/replace namespace "." "-"))

(defn- slashed [namespace]
  (string/replace namespace "." "/"))

(defmacro defpage-r [route & body]
  (let [ns-prefix# (view-ns *ns*)]
    `(noir.core/defpage ~(symbol (str (dashed ns-prefix#) "-" route)) ~((keyword (str (slashed ns-prefix#) "/" route)) routes) ~@body)))

;;----------
;; src/url_for_alternative/views/view_a.clj
;;----------
(ns url-for-alternative.views.view-a
  (:require [url-for-alternative.views.common :as common]
            [url-for-alternative.views.view-b :as view-b])  
  (:use noir.core
        hiccup.core
        url-for-alternative.views.routes))

(defpage-r list []
         (common/layout
           [:p "This is View A"]
           [:a {:href (url-for-r :view-b/list)} "Go to View B"]))

;;----------
;; src/url_for_alternative/views/view_b.clj
;;----------
(ns url-for-alternative.views.view-b
  (:require [url-for-alternative.views.common :as common]
            [url-for-alternative.views.view-a :as view-a])
  (:use noir.core
        hiccup.core
        url-for-alternative.views.routes))

(defpage list []
         (common/layout
           [:p "This is View B"]
           [:a {:href (url-for-r :view-a/list)} "Go to View A"]))
```

Lines 1-39 contain the code needed for defining "central" routes. The
`routes` variable maps route names to their path. For the path, you
can write the exact same code that you would write for `defpage`, for
example `[:get ["/user/:id" :id #"\d+"]]`.

`url-for-r` largely copies noir's `url-for` method, with the exception
that it expects a keyword and not a function in order to do its path
lookup. You'll need to use one of the keywords defined in the `routes`
map. For example, on line 54 you can see that we're using
`:view-b/list` to identify the route. If your path specification takes
variables, you specify them with a map just as you do with
`url-for`. For example, `(url-for-r :users/show {:id id-var})`.

`view-ns`, `dashed`, and `slashed` are merely helper methods that
probably belong in some utility namespace.

`defpage-r` is merely a wrapper around `defpage`. As you can see on
lines 51 and 66, you use it in almost the same way as you use
`defpage`, except that you don't specify the path.

Note that the naming isn't arbitrary. The keywords you choose for your
route map keys take for the format
`view-namespace/function-name`. `view-namespace` is the part of your
namespace which comes after `views.`. So if your namespace is
`my-awesome-site.views.admin.books`, the view namespace would be
`admin.books`. The below example illustrates this:

```clojure
;;----------
;; src/my_awesome_site/views/routes.clj
;;----------

;; url-for-r, defpage-r, other stuff ommitted
(ns my-awesome-site.views.routes
  (require [clojure.string :as string]))

(def routes '{:admin/books/list "/admin/books"
              :admin/books/show    [:get ["/admin/books/:id" :id #"\d+"]]
              :admin/books/edit    "/admin/books/:id/edit"
              :admin/books/update  [:post ["/admin/books/:id" :id #"\d+"]]

              :books/list       "/books"
              :books/show          [:get ["/books/:id" :id #"\d+"]]})

;;----------
;; src/my_awesome_site/views/admin/books.clj
;;----------
(ns url-for-alternative.views.admin.books
  (:require [my-awesome-site.views.common :as common]
            [my-awesome-site.views.books :as books])
  
  (:use noir.core
        hiccup.core
        my-awesome-site.views.routes))

(defpage-r list []
  (common/layout
   [:h1 "Admin Books"]
   [:div#list
    [:div.book
     [:div.name
      [:a {:href (url-for-r :admin/books/show {:id id-var-which-magically-is-here})} "Book Title"]]
     [:div.actions
      [:a {:href (url-for-r :admin/books/edit {:id id-var-which-magically-is-here})} "Edit"]
      [:a {:href (url-for-r :books/show {:id id-var-which-magically-is-here})} "Preview"]]]]))

(defpage-r show {:keys [id]}
  (let [book (magically-get-book id)]
    (common/layout
     ;; display the book
     )))

(defpage-r update {:as book}
  (let [book (magically-get-book (:id book))]
    ;; update the book
    ))
```

I've only just started writing Clojure a couple weeks ago or so, so I'd love feedback on this. Does it make sense? Is it crazy? Would you use it? Right now I'm only using it on ["OMG! SMACKDOWN!!!"](http://omgsmackdown.com)
