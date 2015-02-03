(ns breakout.cmp.brick)

(def ^:private offset {"blue"   0
                       "orange" 1
                       "red"    2
                       "green"  3})

(defn cmp [{pos :pos color :color w :width h :height :as state}]
  [:div.brick {:class color
               :style {:background-image "url(img/tiles.png)"
                       :background-position (str "0 " (* (offset color) (- h)) "px")
                       :position "absolute"
                       :width w
                       :height h
                       :top (:y pos)
                       :left (:x pos)}}])
