(ns breakout.engine.title
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<! close!]]
            [breakout.engine.mouse :as mouse]))

(def ^:private running (atom false))

(defn- listen-to-mouse-clicks [next-scene!]
  (let [chan (mouse/listen-for-clicks)]
    (go (while @running
          (let [got-click (<! chan)]
            (close! chan)
            ;; TODO have mouse clean up event handlers
            (next-scene! :level))))))

(defn start! [next-scene!]
  (reset! running true)
  (listen-to-mouse-clicks next-scene!))

(defn stop! []
  (reset! running false))
