;; Bond - the spy agent
;; ====================
;; This files registers a single view that renders an HTML document with a
;; pretty graph.
;;
;; All data comes from Redis.  We store 2 kinds of data in Redis:
;; 
;; * "hits"        --- number of recorded events
;; * "hit:[0-9]+"  --- An event hash
;;
;; Each event is a Redis hash that looks like this:
;;
;; user     "honza"
;; added    "20120529"
;; datetime "2012-05-29 18:00:00"
;; url      "/admin/"
;;
;; The graph will show a line for each user.  It will show how many pages the
;; given user hit on a given day.  The graph will show the last 30 days.
;;
;; Bond is licensed under the terms of the BSD license.
;; (c) 2012 - Honza Pokorny - All rights reserved
;;
;; DISCLAIMER: This is my first Clojure project and I'm still very much
;; learning the language and the idioms.

(ns bond.views.redis
  (:require [bond.views.common :as common]
            [clj-redis.client :as redis])
  (:use [noir.core :only [defpage]]
        [cheshire.core]
        [clojure.set]
        [clj-time.core :only [now minus plus days date-time year month day]]
        [hiccup.core :only [html]]))

;; ---------------------------------------------------------------------------

(def db (redis/init {:url (load-string (slurp "bond.config"))}))

(defn hgetall [key] 
    "Retrieve a Redis hash"
  (into {} 
        (for [[k v] (redis/hgetall db key)]
        [(keyword k) v])))

(defn get-hit-list [num] 
  "Get all hits from Redis up to `num`."
    (loop [count num result '()]
        (if (zero? count)
            result
            (recur
                (dec count)
                (cons (hgetall (str "hit:" (Integer/toString count))) result)))))

(defn date-to-string [d]
  "Convert a clj-time datetime object to string."
  (str
        (year d)
        (if (< (month d) 10) (str "0" (month d)) (month d))
        (if (< (day d) 10) (str "0" (day d)) (day d))
    ))

(defn get-last-month [now-now]
  "Give me a list of date-time objects representing the last 30 days"
  (let [d (date-time (year now-now) (month now-now) (day now-now))
        r (range 1 30)]
    (map (fn [x] (minus d (days x))) r)))

(defn sort-hits [list-of-hits]
  "Take a list of hit maps and produce a map with users as keys"
  (group-by :user list-of-hits))

(defn make-user-timeline [user-items last-month-items]
  "What is the data structure?"  
  ;; For every day in the last month
  ;; See if there is a key in `sorted-user-items`
  ;; Create a list of maps:
  ;; ({:added 20120526 :count 2})

  (map (fn [m]
        {:added m :count (count (get user-items m))})
       (reverse last-month-items)))

(defn get-all-data [sorted-hits last-month]
  "Prepare data for serialization"
    (letfn [(make-timeline [uid]
                (make-user-timeline (group-by :added (get sorted-hits uid))
                                    last-month))]
        (into {} (map (juxt keyword make-timeline)
                    (keys sorted-hits)))))


;; Views ---------------------------------------------------------------------

(defpage "/" []
    (let [x             (Integer/parseInt (redis/get db "hits"))
          last-month    (map date-to-string (get-last-month (plus (now) (days 2))))
          sorted-hits   (sort-hits (get-hit-list x))] 

        (common/layout
            (common/json 
                (generate-string
                    (get-all-data sorted-hits last-month))))))
