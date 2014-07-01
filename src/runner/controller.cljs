(ns runner.controller
  (:require [cljs.core.async :refer [put! chan <! >! alts!] :as async]
            [goog.events :as events]
            [goog.events.KeyCodes :as KeyCodes])
  (:require-macros [cljs.core.async.macros :refer [go alt! go-loop]]))


(def player-controls {:jump KeyCodes/SPACE
                      :left KeyCodes/LEFT
                      :right KeyCodes/RIGHT})

(defn specific-key
  "Create a callback fn that can be used by events/listen"
  [keycode channel status]
  (fn [e]
    (when (= (.-keyCode e) (player-controls keycode))
      (put! channel status))))


(defn key-channel
  "Construct a channel that will say when a specific key is being pressed"
  [keycode]
  (let [channel (chan)]
    (events/listen js/window "keydown" (specific-key keycode channel :on))
    (events/listen js/window "keyup" (specific-key keycode channel :off))
    channel))


(defn update-key
  "construct a fn that's used for updating the state of the player controller"
  [key-pressed key-direction]
  (fn [controller-state]
    (update-in controller-state
               [:controller key-pressed] (fn [_] key-direction))))

(defn controller-keys
  "construct a string that specifies the state of the controller keys"
  [controller]
  (str "Left: " (:left controller) ", Right: " (:right controller) ", Jump: " (:jump controller)))


(def controller-state
  (let [left-chan (key-channel :left)
        right-chan (key-channel :right)
        jump-chan (key-channel :jump)
        app-state (atom {:controller {:left :off
                                      :right :off
                                      :jump :off}})]
    (go (while true
          (let [[v c] (alts! [left-chan right-chan jump-chan])]
            (condp = c
              left-chan (swap! app-state (update-key :left v))
              right-chan (swap! app-state (update-key :right v))
              jump-chan (swap! app-state (update-key :jump v))))))
    app-state))
