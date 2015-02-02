(ns breakout.engine.mouse
  (:require [cljs.core.async :refer [put! chan]]
            [goog.events :as events]
            [goog.events.EventType :as EventType]))

(defn- channel-for-event [event handler]
  (let [out (chan)]
    (events/listen js/document event #(handler % out))
    out))

(defn listen-to-movement []
  (channel-for-event EventType/MOUSEMOVE
                     (fn [e out]
                       (when-let [el (.getElementById js/document "board")]
                         (put! out (- (.-clientX e) (.-offsetLeft el)))))))

(defn listen-for-clicks []
  (channel-for-event EventType/CLICK
                     (fn [e out]
                       (put! out true))))

