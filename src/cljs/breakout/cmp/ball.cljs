(ns breakout.cmp.ball)

(defn cmp [{x :x y :y}]
  [:div.ball {:style {:background-image "url(img/tiles.png)"
                      :position "absolute"
                      :width 16
                      :height 16
                      :top y
                      :left x}}])
