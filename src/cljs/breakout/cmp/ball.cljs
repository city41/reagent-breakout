(ns breakout.cmp.ball)

(defn cmp [pos size]
  [:div.ball {:style {:background-image "url(img/tiles.png)"
                      :position "absolute"
                      :width (:width size)
                      :height (:height size)
                      :top (:y pos)
                      :left (:x pos)}}])
