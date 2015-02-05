(ns breakout.style
  (:require [garden.def :refer [defstylesheet defstyles defkeyframes]]
            [garden.units :refer [px em percent s]]))

(defkeyframes ball-spin
  [:from
   {:background-position [[(px -48) (px -64)]]}]
  [:to
   {:background-position [[(px -112) (px -64)]]}])

(defkeyframes blue-brick-spawn
  [:from {:background-position [[(px -128) 0]]}]
  [:to {:background-position [[0 0]]}])

(defkeyframes orange-brick-spawn
  [:from {:background-position [[(px -128) (px -16)]]}]
  [:to {:background-position [[0 (px -16)]]}])

(defkeyframes red-brick-spawn
  [:from {:background-position [[(px -128) (px -32)]]}]
  [:to {:background-position [[0 (px -32)]]}])

(defkeyframes green-brick-spawn
  [:from {:background-position [[(px -128) (px -48)]]}]
  [:to {:background-position [[0 (px -48)]]}])

(defkeyframes countdown
  [:from {:background-position [[0 (px -96)]]}]
  [:to {:background-position [[(px -64) (px -96)]]}])

(defn- bricks-enter []
  (let [colors ["blue" "orange" "red" "green"]]
    (for [color colors]
      [(str ".brick." color ".spawn-enter")
       ^:prefix {:animation-name (str color "-brick-spawn")
                 :animation-timing-function "steps(4)"
                 :animation-duration (s 0.33)
                 :animation-iteration-count 1}])))

(defn- bricks-leave []
  (let [colors ["blue" "orange" "red" "green"]]
    (for [color colors]
      [(str ".brick." color ".spawn-leave")
       ^:prefix {:animation-name (str color "-brick-spawn")
                 :animation-direction :reverse
                 :animation-timing-function "steps(4)"
                 :animation-duration (s 0.33)
                 :animation-iteration-count 1}])))

(defstyles stylesheet
  [:body
   {:font-family ["Helvetica Neue" "Verdana" "Heveltica" "Arial" "sans-serif"]
    :margin 0
    :padding 0
    :font-size (em 1.125)
    :background-color "black"}]
  
  [:#app
   {:width (px 320)
    :cursor :none
    :margin-left :auto
    :margin-right :auto
    :margin-top (px 60)}]
  
  [:.ball
   {:background-position [[(px -48) (px -64)]]}
   ^:prefix {:animation-name ball-spin
             :animation-duration (s 0.5)
             :animation-timing-function "steps(4)"
             :animation-iteration-count "infinite"}]

  [:.countdown
   {:background-position [[(px -64) (px -96)]]}
   ^:prefix {:animation-name countdown
             :animation-duration (s 3)
             :animation-iteration-count 1
             :animation-timing-function "steps(2)"}]

  (bricks-enter)
  (bricks-leave)
  
  ball-spin
  blue-brick-spawn
  orange-brick-spawn
  red-brick-spawn
  green-brick-spawn
  countdown)



