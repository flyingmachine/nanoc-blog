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