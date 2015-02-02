(ns breakout.cmp.logo-bg)

(defn cmp [label]
  [:div#title {:style {:position "relative"
                       :background-image "url(img/bg_prerendered.png)"
                       :width 320
                       :height 412}}
   [:div.logo {:style {:position "absolute"
                       :background-image "url(img/logo.png)"
                       :width 131
                       :height 200
                       :top 59
                       :left 94.5}}]
   [:div {:style {:position "absolute"
                  :bottom 100
                  :left 0
                  :right 0
                  :text-align "center"}}
    label]])
