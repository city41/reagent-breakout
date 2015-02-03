(ns breakout.cmp.paddle)

(defn cmp [pos]
  [:div.paddle {:style {:background-image "url(img/tiles.png)"
                        :background-position "0 -64px"
                        :width "48px"
                        :height "16px"
                        :left (:x pos)
                        :top (:y pos)
                        :position "absolute"}}])
