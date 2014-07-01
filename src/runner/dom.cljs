(ns runner.dom
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))


;; in-game utils
;; ----------------------------------------------------------------------------

(defn player-coordinates [player owner]
  (reify
    om/IRender
    (render [_]
            (dom/div #js {:className "coordinates"}
                     (dom/p nil (str "x: " (:x player)))
                     (dom/p nil (str "y: " (:y player)))))))

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


;; screen coordinates
;; ----------------------------------------------------------------------------

(def physics-to-pixel 80)

(def screen-width (fn [_] js/window.innerWidth))
(def screen-height (fn [_] js/window.innerHeight))


;; player view
;; ----------------------------------------------------------------------------

(defn player-width
  "on-screen width"
  [radius]
  (* radius physics-to-pixel 2))

(defn ps-size [radius]
  (let [width (player-width radius)
        radius (/ width 2)]
    {:width width
     :height width
     :-webkit-border-radius radius
     :-moz-border-radius radius
     :border-radius radius}))

(defn player-x
  "on-screen x position"
  [radius]
  (- (/ (screen-width) 2) (* radius physics-to-pixel)))

(defn ps-position [x y radius]
  {:bottom (js/Math.round (* physics-to-pixel y))
   :left (js/Math.round (player-x radius))})

(defn player-style
  "construct a style string from a js array. e.g position = #js [0, 9.8]"
  [player]
  (clj->js (merge {:position "absolute"
                  :background-color "orange"}
                 (ps-size (:radius player))
                 (ps-position (:x player) (:y player) (:radius player)))))


(defn player-view [player owner]
  (reify
    om/IRender
    (render [_]
            (dom/div #js {:style (player-style player)} "player"))))


;; game view/wrapper
;; ----------------------------------------------------------------------------

(defn game-view [state owner]
  (reify
    om/IRender
    (render [_]
            (dom/div nil
                     (om/build player-coordinates (:player state))
                     (om/build player-view (:player state))))))



