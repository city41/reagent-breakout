(ns breakout.cmp.hud)

(defn cmp [level score lives]
  [:div {:style {:position "absolute"
                 :text-align "center"
                 :left 20
                 :right 20
                 :bottom 3}}
   (str "lives: " lives " score: " score " level: " (inc level))])
