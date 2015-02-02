(ns breakout.engine.game-over
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! close!]]
            [breakout.engine.mouse :as mouse]))

(def ^:private running (atom false))

(defn- set-timeout [next-scene!]
  (.setTimeout js/window 
               #(when @running (next-scene! :title))
               3000))

(defn- listen-to-mouse-clicks [next-scene!]
  (let [chan (mouse/listen-for-clicks)]
    (go (while @running
          (let [got-click (<! chan)]
            (close! chan)
            ;; TODO have mouse clean up event handlers
            (next-scene! :title))))))

(defn start! [next-scene!]
  (reset! running true)
  (set-timeout next-scene!)
  (listen-to-mouse-clicks next-scene!))

(defn stop! []
  (reset! running false))

