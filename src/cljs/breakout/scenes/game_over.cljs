(ns breakout.scenes.game-over
  (:require [breakout.cmp.logo-bg :as title]))

(defn scene []
  [title/cmp "game over!"])

