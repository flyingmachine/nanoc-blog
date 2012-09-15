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