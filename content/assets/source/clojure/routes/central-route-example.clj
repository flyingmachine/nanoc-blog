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