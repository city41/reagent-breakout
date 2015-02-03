(ns breakout.core
  (:require [reagent.core :as reagent]
            [breakout.engine.main :as main]
            [breakout.scenes.level :as level]
            [breakout.scenes.title :as title]
            [breakout.scenes.game-over :as game-over]
            [breakout.scenes.win :as win]))

(def scenes {:level     level/scene
             :title     title/scene
             :game-over game-over/scene
             :win       win/scene})

(defn container [scene]
  [:div [(scenes @scene)]])

(defn init! []
  (main/start!)
  (reagent/render-component [container main/current-scene] (.getElementById js/document "app")))
