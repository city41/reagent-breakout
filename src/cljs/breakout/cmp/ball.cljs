(ns breakout.cmp.ball
  (:require [reagent.core :refer [atom]]))

(def frame (atom 0))

(defn move-frame []
  (reset! frame (mod (inc @frame) 4))
  (.setTimeout js/window move-frame 100))

(defn cmp [pos size]
  (move-frame)
  (fn [pos size]
    [:div.ball {:style {:background-image "url(img/tiles.png)"
                        :background-position [(- -48 (* @frame 16)) -64]
                        :position "absolute"
                        :width (:width size)
                        :height (:height size)
                        :top (:y pos)
                        :left (:x pos)}}]))
