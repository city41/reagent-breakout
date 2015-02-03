(ns breakout.engine.level
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :refer [<!]]
            [reagent.core :refer [atom]]
            [breakout.engine.mouse :as mouse]
            [breakout.levels.data :as levels :refer [brick-width brick-height]])
  (:import goog.math.Rect))

;; --- constants
(def tile-size 16)
(def board {:width 320 :height 416})
(def paddle-y (- (:height board) (* 3 tile-size)))
(def ball-size {:width tile-size :height tile-size})
(def paddle-size {:width 48 :height 16})
(def starting-ball-pos {:x (* 2 tile-size) :y (* 15 tile-size)})
(def base-vel (/ 120 1000)) ; 120 pixels per second
(def starting-ball-vel {:x base-vel :y base-vel})
(def countdown-duration (atom 3000))

;; --- state, there's a lot of it (this is a game after all)
(def phase (atom nil))
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
  (let [pos (update-in pos [:x] + (* delta (:x vel)))]
    (update-in pos [:y] + (* delta (:y vel)))))

(defn- beyond-board? [pos]
  (>= (:y pos) (:height board)))

(defn- flip! [vel-atom key]
  (swap! vel-atom assoc key (- (key @vel-atom))))

(defn- get-center-x [pos size]
  (+ (/ (:width size) 2) (:x pos)))

(defn- get-flip-direction [ball-pos brick]
  (let [cbx (get-center-x ball-pos ball-size)
        left (get-in brick [:pos :x])
        right (+ left (:width brick))]
    (or
      (and (> cbx left) (< cbx right) :y)
      :x)))

;; determines the x velocity for the ball based on where
;; on the paddle the ball struck. The closer to the center
;; of the paddle, the closer to zero the x velocity
(defn- get-x-vel-from-paddle-bounce [ball-pos paddle-pos]
  (let [half-paddle (/ (:width paddle-size) 2)
        cbx (get-center-x ball-pos ball-size)
        cpx (get-center-x paddle-pos paddle-size)
        distance (- cbx cpx)
        ratio (/ distance half-paddle)]
    (* 1.5 base-vel ratio))
  )

(defn- setup-next-level! [level]
  (let [brick-data (levels/get-level-data level)]
    (when brick-data
      ;; small hack -- by delaying the brick data, it allows
      ;; React's CSSTransitionGroup to kick in, causing the bricks
      ;; to appear on the board with a CSS animation
      (.setTimeout js/window #(reset! bricks brick-data) 100)
      (reset! phase :countdown)
      true)))

;; --- state initialization
;; called whenever a phase transition within gameplay happens
(defmulti init-phase! identity)

(defmethod init-phase! :countdown [_]
  (reset! ball-pos starting-ball-pos)
  (reset! countdown-duration 3000))

(defmethod init-phase! :gameplay [_]
  (reset! ball-pos starting-ball-pos)
  (reset! ball-vel starting-ball-vel))

(add-watch phase :scene-phase
           (fn [key r old-phase new-phase]
             (when new-phase
               (init-phase! new-phase))))

;; --- updating phases
;; called once per frame to update the current phase
(defmulti update-phase! (fn [delta phase] phase))

(defmethod update-phase! :countdown [delta _]
  (swap! countdown-duration - delta)
  (when (<= @countdown-duration 0)
    (reset! phase :gameplay)))

(defmethod update-phase! :gameplay [delta _]
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
                 (flip! ball-vel (get-flip-direction new-pos collided-object))
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
                     (reset! phase :countdown)))
                 (reset! ball-pos new-pos)))))

(defn- update! [ts]
  (when @running
    (let [delta (- ts (or @last-ts ts))]
      (reset! last-ts ts)
      (update-phase! delta @phase))
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
  (reset! bricks #{})
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

