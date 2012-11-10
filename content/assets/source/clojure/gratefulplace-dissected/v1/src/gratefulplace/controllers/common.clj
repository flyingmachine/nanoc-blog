(ns gratefulplace.controllers.common
  (:require [cemerick.friend :as friend])
  (:use gratefulplace.utils
        gratefulplace.models.permissions))

;; validation: combination of field name and validation checks
;;
;; validation check group: first member is an error message, the rest
;; of the members are validation checks. If any validation check
;; returns false then the error message is added to a list of error
;; messages for the given field.
;;
;; validation check: a function to apply to the value corresponding to
;; the field name specified in the validation

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

;; TODO it might be better to use this than if-valid
(defn validation-failures
  [to-validate validations]
  (self-unless-fn (validate to-validate validations) empty? nil))

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

(defmacro with-visibility
  "this conditional was used a few times so I put it in a macro"
  [current-auth {:keys [moderator logged-in not-logged-in]}]
  `(let [current-auth# ~current-auth]
     (cond (moderator? (:username current-auth#)) ~moderator
           current-auth# ~logged-in
           :else ~not-logged-in)))