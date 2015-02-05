(ns breakout.engine.title
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! close!]]
            [breakout.engine.input :as input]))

(def ^:private running (atom false))

(defn- listen-to-input-clicks [next-scene!]
  (go 
    (while @running
      (<! input/clicks)
      (next-scene! :level))))

(defn start! [next-scene!]
  (reset! running true)
  (listen-to-input-clicks next-scene!))

(defn stop! []
  (reset! running false))
