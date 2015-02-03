(ns breakout.cmp.brick)

(def ^:private offset {"blue"   0
                       "orange" 1
                       "red"    2
                       "green"  3})

(defn cmp [{pos :pos color :color}]
  [:div.brick {:class color
               :style {:background-image "url(img/tiles.png)"
                       :background-position (str "0 " (* (offset color) -16) "px")
                       :position "absolute"
                       :width 32
                       :height 16
                       :top (:y pos)
                       :left (:x pos)}}])
