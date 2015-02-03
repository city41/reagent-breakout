(ns breakout.cmp.countdown)

(defn cmp []
  [:div.countdown {:style {:position "absolute"
                           :top 200
                           :left 144
                           :width 32
                           :height 48
                           :background-image "url(img/tiles.png)"}}])
