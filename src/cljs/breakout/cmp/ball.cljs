(ns breakout.cmp.ball
  (:require [reagent.core :refer [atom]]))

(defn cmp [pos size]
  (fn [pos size]
    [:div.ball {:style {:background-image "url(img/tiles.png)"
                        :position "absolute"
                        :width (:width size)
                        :height (:height size)
                        :top (:y pos)
                        :left (:x pos)}}]))
