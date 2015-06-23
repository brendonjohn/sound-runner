(ns runner.core
  (:require [cljs.core.async :refer [<!] :as async]
            [goog.events :as events]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [runner.physics :refer [create-player physics-chan main-player player-radius]]
            [runner.dom :refer [game-view controller-view]]
            [runner.controller :refer [controller-state]])
  (:require-macros [cljs.core.async.macros :refer [go alt! go-loop]]))

(enable-console-print!)


;; player state
;; ----------------------------------------------------------------------------

(def app-state (atom {:player {:x (aget (.-position main-player) 0)
                               :y (aget (.-position main-player) 1)
                               :radius player-radius}}))

(def xmovement 20)
(def ymovement 40)
(defn player-movement [controller]
  (let [{:keys [left right jump]} controller]
    {:dx (if (= left :on) (- xmovement) (if (= right :on) xmovement 0))
     :dy (if (= jump :on) ymovement 0)}))


(defn update-player [direction]
  (fn [app-state]
    (assoc-in app-state
              [:player direction]
              (aget (.-position main-player)
                    (condp = direction
                      :x 0
                      :y 1)))))


;; tie om component to data
;; ----------------------------------------------------------------------------


(go (while true
      ;;wait for physics world to say that it has updated
      (<! physics-chan)
      ;; Set the new player state
      (swap! app-state (update-player :x))
      (swap! app-state (update-player :y))
      ;; adjust player velocity by the controller
      (let [{:keys [dx dy]} (player-movement (:controller @controller-state))]
        (aset (.-force main-player) 1 (+ (aget (.-force main-player) 1) dy))
        (aset (.-force main-player) 0 (+ (aget (.-force main-player) 0) dx)))))
(om/root game-view app-state
         {:target (. js/document (getElementById "game-wrapper"))})

(om/root controller-view controller-state
         {:target (. js/document (getElementById "controller"))})


