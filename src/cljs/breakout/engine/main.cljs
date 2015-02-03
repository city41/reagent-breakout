(ns breakout.engine.main
  (:require [reagent.core :refer [atom]]
            [breakout.engine.title :as title]
            [breakout.engine.level :as level]
            [breakout.engine.game-over :as game-over]))

(def ^:private starts {:title     title/start!
                       :level     level/start!
                       :win       game-over/start!
                       :game-over game-over/start!})

(def ^:private stops {:title     title/stop!
                      :level     level/stop!
                      :win       game-over/stop!
                      :game-over game-over/stop!})

(def current-scene (atom nil))

(defn- set-next-scene! [next-scene]
  (reset! current-scene next-scene))

(add-watch current-scene :current-scene 
           (fn [_ _ old-scene next-scene]
             (when old-scene
               ((stops old-scene)))
             ((starts next-scene) set-next-scene!)))

(defn start! []
  (reset! current-scene :title))


