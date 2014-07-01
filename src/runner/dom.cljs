(ns runner.dom
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


(defn player-style
  "construct a style string from a js array. e.g position = #js [0, 9.8]"
  [player]
  #js {:position "absolute"
       :bottom (js/Math.round (* 80 (:y player)))
       :left (js/Math.round (* 80 (:x player)))})

(defn player-coordinates [player owner]
  (reify
    om/IRender
    (render [_]
            (dom/div #js {:className "coordinates"}
                     (dom/p nil (str "x: " (:x player)))
                     (dom/p nil (str "y: " (:y player)))))))

(defn player-view [player owner]
  (reify
    om/IRender
    (render [_]
            (dom/div #js {:style (player-style player)
                          :className "player"} "player"))))


(defn game-view [state owner]
  (reify
    om/IRender
    (render [_]
            (dom/div nil
                     (om/build player-coordinates (:player state))
                     (om/build player-view (:player state))))))


(defn controller-keys
  "construct a string that specifies the state of the controller keys"
  [controller]
  (str "Left: " (:left controller) ", Right: " (:right controller) ", Jump: " (:jump controller)))


(defn controller-view
  "view for displaying what the player controller is doing"
  [state owner]
  (reify
    om/IRender
    (render [_]
            (dom/div nil
                     (dom/p nil (controller-keys (:controller state)))))))
