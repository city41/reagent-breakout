(ns breakout.engine.level
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [reagent.core :refer [atom]]
            [breakout.engine.mouse :as mouse]
            [breakout.levels.core :as levels :refer [brick-width brick-height]])
  (:import goog.math.Rect))

;; --- constants
(def tile-size 16)
(def board {:width 320 :height 416})
(def paddle-y (- (:height board) (* 3 tile-size)))
(def ball-size {:width tile-size :height tile-size})
(def paddle-size {:width 48 :height 16})
(def starting-ball-pos {:x (* 2 tile-size) :y (* 15 tile-size)})
(def starting-ball-vel {:x 2 :y 2})
(def countdown-duration (atom 3000))

;; --- state, there's a lot of it (this is a game after all)
(def state (atom nil))
(def running (atom nil))
(def last-ts (atom nil))
(def next-scene! (atom nil))
(def score (atom nil))
(def lives (atom nil))
(def level (atom nil))
(def bricks (atom nil))
(def paddle-pos (atom {:x 0 :y paddle-y}))
(def ball-pos (atom starting-ball-pos))
(def ball-vel (atom starting-ball-vel))

;; --- collision related
;; these functions detect collision and are called from update-state! :gameplay
(def walls [(Rect. 0 0 tile-size (:height board))
            (Rect. (- (:width board) tile-size) 0 tile-size (:height board))])

(def ceiling (Rect. 0 0 (:width board) tile-size))

(defn pos->rect [pos size]
  (Rect. (:x pos) (:y pos) (:width size) (:height size)))

(defn get-collided-brick [ball-rect bricks]
  (first
    (filter #(.intersects (:rect %) ball-rect) bricks)))

(defn- rect-collided-with [src-rect rects]
  (some #(.intersects % src-rect) rects))

(defn- get-collision [pos bricks walls ceiling paddle-pos]
  (let [ball-rect (pos->rect pos ball-size)
        collided-brick (get-collided-brick ball-rect bricks)]
    (or
      (and collided-brick [:brick collided-brick])
      (and (rect-collided-with ball-rect walls) [:wall])
      (and (rect-collided-with ball-rect [ceiling]) [:ceiling])
      (and (rect-collided-with ball-rect [(pos->rect paddle-pos paddle-size)]) [:paddle])
      [:none])))

(defn- move-ball [delta pos vel]
  (let [pos (update-in pos [:x] + (:x vel))]
    (update-in pos [:y] + (:y vel))))

(defn- beyond-board? [pos]
  (>= (:y pos) (:height board)))

(defn- flip! [vel-atom key]
  (swap! vel-atom assoc key (- (key @vel-atom))))

;; determines the x velocity for the ball based on where
;; on the paddle the ball struck. The closer to the center
;; of the paddle, the closer to zero the x velocity
(defn- get-x-vel-from-paddle-bounce [ball-pos paddle-pos]
  (let [half-paddle (/ (:width paddle-size) 2)
        cbx (+ (/ tile-size 2) (:x ball-pos))
        cpx (+ half-paddle (:x paddle-pos))
        distance (- cbx cpx)
        ratio (/ distance half-paddle)]
    (* 2.5 ratio))
  )

(defn- setup-next-level! [level]
  (let [brick-data (levels/get-level-data level)]
    (when brick-data
      (reset! bricks brick-data)
      (reset! state :countdown)
      true)))

;; --- state initialization
;; called whenever a state transition within gameplay happens
(defmulti init-state! identity)

(defmethod init-state! :countdown [_]
  (reset! ball-pos starting-ball-pos)
  (reset! countdown-duration 3000))

(defmethod init-state! :gameplay [_]
  (reset! ball-pos starting-ball-pos)
  (reset! ball-vel starting-ball-vel))

(add-watch state :scene-state
           (fn [key r old-state new-state]
             (when new-state
               (init-state! new-state))))

;; --- updating states
;; called once per frame to update the current state
(defmulti update-state! (fn [delta state] state))

(defmethod update-state! :countdown [delta _]
  (swap! countdown-duration - delta)
  (when (<= @countdown-duration 0)
    (reset! state :gameplay)))

(defmethod update-state! :gameplay [delta _]
  (let [old-pos @ball-pos
        new-pos (move-ball delta old-pos @ball-vel)
        pad-pos @paddle-pos
        [collided-type collided-object] (get-collision new-pos @bricks walls ceiling pad-pos)]
    (case collided-type
      :wall    (flip! ball-vel :x)
      :ceiling (flip! ball-vel :y)
      :paddle  (do
                 (flip! ball-vel :y)
                 (swap! ball-vel assoc :x (get-x-vel-from-paddle-bounce new-pos pad-pos)))
      :brick   (do
                 (flip! ball-vel :x)
                 (flip! ball-vel :y)
                 (swap! score + 100)
                 (swap! bricks disj collided-object)
                 (when (zero? (count @bricks))
                   (let [next-level (swap! level inc)]
                     (when-not (setup-next-level! next-level)
                       (@next-scene! :win)))))
      :none    (if (beyond-board? new-pos)
                 (let [remaining-lives (swap! lives dec)]
                   (if (<= remaining-lives 0)
                     (@next-scene! :game-over)
                     (reset! state :countdown)))
                 (reset! ball-pos new-pos)))))

(defn- update! [ts]
  (when @running
    (let [delta (- ts (or @last-ts ts))]
      (reset! last-ts ts)
      (update-state! delta @state))
    (. js/window (requestAnimationFrame update!))))

(defn- listen-to-mouse-moves! []
  (let [chan (mouse/listen-to-movement)
        clamp (fn [v vmin vmax]
                (min vmax (max v vmin)))]
    (go (while @running
          (let [mouse-x (<! chan)
                mouse-x (clamp mouse-x 0 (- 320 48))]
            (swap! paddle-pos assoc :x mouse-x))))))


(defn- init! []
  (reset! lives 3)
  (reset! score 0)
  (reset! level 0)
  (setup-next-level! 0)
  (reset! last-ts nil)
  (reset! running true))

(defn start! [set-next-scene!]
  (reset! next-scene! set-next-scene!)
  (init!)
  (listen-to-mouse-moves!)
  (. js/window (requestAnimationFrame update!)))

(defn stop! []
  (reset! running false))

