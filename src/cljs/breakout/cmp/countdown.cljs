(ns breakout.cmp.countdown)

(defn- get-frame [c]
  (->> c
       (- 3000)
       (* .001)
       (.floor js/Math)))

(defn cmp [count]
  (let [frame (get-frame count)] 
    [:div.countdown {:style {:position "absolute"
                             :top 200
                             :left 144
                             :width 32
                             :height 48
                             :background-image "url(img/tiles.png)"
                             :background-position (str (- (* frame 32)) "px -96px")}}]))
