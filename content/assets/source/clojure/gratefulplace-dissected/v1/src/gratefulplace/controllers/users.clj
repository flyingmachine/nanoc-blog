(ns gratefulplace.controllers.users
  (:require [gratefulplace.models.user :as user]
            [gratefulplace.models.post :as post]
            [gratefulplace.models.comment :as comment]
            [gratefulplace.views.users :as view]
            [ring.util.response :as res]
            [cemerick.friend :as friend]
            [cemerick.friend.workflows :as workflows])

  (:use [gratefulplace.controllers.common :only (if-valid view)]
        gratefulplace.models.permissions))


(defn show-new
  [req]
  (view view/show-new))

(defn user-from-req
  [req]
  (user/for-user-page (:params req)))

(defn create!
  [req]
  (let [{:keys [uri request-method params]} req]
    (when (and (= uri "/users")
               (= request-method :post))
      (if-valid
       params
       (:create user/validation-contexts)
       errors
       
       (workflows/make-auth (user/create! params))
       {:body (view view/show-new :errors errors)}))))

(defn show
  [req]
  (view
   view/show
   :user (user-from-req req)))

(defn posts
  [req]
  (let [user (user-from-req req)]
    (view
     view/posts
     :user  user
     :posts (post/all (korma.core/where {:user_id (:id user)})))))

(defn comments
  [req]
  (let [user (user-from-req req)]
    (println user)
    (view
     view/comments
     :user user
     :comments (comment/all (korma.core/where {:user_id (:id user)})))))

(defn edit
  [req]
  (let [username (get-in req [:params :username])]
    (protect
     (can-modify-profile? username)
     (view
      view/edit
      :user (user/one {:username username})))))

;; TODO don't really need to have a redirect here do I?
(defn update
  [req]
  (let [params   (:params req)
        username (:username params)]
    (protect
     (can-modify-profile? username)
     (let [validations (cond
                        (:change-password params)
                        ((:change-password user/validation-contexts)
                         (let [user (user/one {:username username})] (:password user)))
                        
                        (:email params)
                        (:update-email user/validation-contexts)
                        
                        :else {})]
       
       (if-valid
        params validations errors
        (let [new-attributes (if (:change-password params)
                               {:password (get-in params [:change-password :new-password])}
                               (dissoc params :username))]
          (user/update! {:username username} new-attributes)
          (res/redirect (str "/users/" username "/edit?success=true")))
        (view
         view/edit
         :user params
         :errors errors))))))