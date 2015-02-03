(ns breakout.cmp.paddle)

(defn cmp [{x :x y :y}]
  [:div.paddle {:style {:position "absolute"
                        :background-image "url(img/tiles.png)"
                        :background-position "0 -64px"
                        :width 48
                        :height 16
                        :left x
                        :top y}}])
