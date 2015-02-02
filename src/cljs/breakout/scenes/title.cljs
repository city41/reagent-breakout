(ns breakout.scenes.title
  (:require [breakout.cmp.logo-bg :as title]))

(defn scene []
  [title/cmp "click to begin"])
