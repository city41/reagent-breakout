(ns breakout.engine.game-over
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! close!]]
            [breakout.engine.input :as input]))

(def ^:private running (atom false))

(defn- set-timeout [next-scene!]
  (.setTimeout js/window 
               #(when @running (next-scene! :title))
               3000))

(defn- listen-to-input-clicks [next-scene!]
  (go 
    (while @running
      (<! input/clicks)
      (next-scene! :title))))

(defn start! [next-scene!]
  (reset! running true)
  (set-timeout next-scene!)
  (listen-to-input-clicks next-scene!))

(defn stop! []
  (reset! running false))

