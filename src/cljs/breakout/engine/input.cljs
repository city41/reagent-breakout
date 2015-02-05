(ns breakout.engine.input
  (:require [cljs.core.async :refer [put! chan]]
            [goog.events :as events]
            [goog.events.EventType :as EventType]))

(defn- events->chan [el event-type c]
  (events/listen el event-type #(put! c %))
  c)

(def app-el (.getElementById js/document "app"))
(def has-touch (boolean js/window.ontouchstart))
(def MOVE (if has-touch EventType/TOUCHMOVE EventType/MOUSEMOVE))
(def CLICK (if has-touch "tap" EventType/CLICK))

(defn- subtract-offset [e]
  (- (.-clientX e) (.-offsetLeft app-el)))

(defn- clamp [vmin vmax v]
  (min vmax (max v vmin)))

(def movement (events->chan app-el MOVE
                            (chan 1 (comp
                                      (map subtract-offset)
                                      (map (partial clamp 0 272))))))

(def clicks (events->chan app-el CLICK (chan)))


