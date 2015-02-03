(ns breakout.cmp.board
  (:require [breakout.cmp.ball :as ball]
            [breakout.cmp.countdown :as countdown]
            [breakout.cmp.brick :as brick]
            [breakout.cmp.paddle :as paddle]
            [breakout.cmp.hud :as hud]))

(def ctg (aget js/React "addons" "CSSTransitionGroup"))

(defn cmp [ball-pos ball-size bricks paddle-pos level score lives phase]
  [:div#board {:style {:position "relative"
                       :background-image "url(img/bg_prerendered.png)"
                       :width 320
                       :height 412}} 
   (when (= @phase :countdown)
     [countdown/cmp])
   (when @ball-pos
     [ball/cmp @ball-pos ball-size])
   [ctg {:transitionName "spawn"}
    (for [brick @bricks]
      ^{:key brick} [brick/cmp brick])]
   [paddle/cmp @paddle-pos]
   [hud/cmp @level @score @lives]])
