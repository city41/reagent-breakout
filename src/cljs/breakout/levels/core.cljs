(ns breakout.levels.core
  (:import goog.math.Rect))

(def ^:private X nil)
(def ^:private g "green")
(def ^:private o "orange")
(def ^:private b "blue")
(def ^:private r "red")

(def brick-width 32)
(def brick-height 16)
(def ^:private corner-x (* brick-width 1.5))
(def ^:private corner-y (* brick-height 4))

(def levels {0 [
                [X X g o g X X]
                [o b g g g b o]
                [X b b b b b X]
                ]
             1 [
                [X g o g o g X]
                [X b b b b b X]
                [g b r b r b g]
                [g b b b b b g]
                [g b X X X b g]
                [X b b b b b X]
                ]})

(defn get-brick-data [x y color]
  (when color
    (let [x (+ corner-x (* brick-width x))
          y (+ corner-y (* brick-height y))] 
      {:color color
       :width brick-width
       :height brick-height
       :pos {:x x :y y}
       :rect (Rect. x y brick-width brick-height)})))

(defn get-row-data [y row]
  (map-indexed #(get-brick-data %1 y %2) row))

(defn get-level-data [level]
  (when (contains? levels level)
    (disj
      (into #{} (flatten (map-indexed get-row-data (levels level))))
      nil)))
