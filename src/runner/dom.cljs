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

(def screen-width (fn [] js/window.innerWidth))
(def screen-height (fn [] js/window.innerHeight))


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


;; background elements
;; ----------------------------------------------------------------------------

(defn square-position [squarex squarey playerx playery]
  {:left (- squarex (* physics-to-pixel playerx))
   :bottom (- squarey (* playery 0.01 squarey))})

(defn square-rotation [squarex squarey playerx playery]
  (let [rotation (* 2 (- squarex (* (/ physics-to-pixel 4) playerx)))
        rotate (str "rotate(" rotation "deg)")
        origin "40% 40%"]
    {:transform rotate
     :transform-origin origin
     :-ms-transform rotate ;IE 9
     :-ms-transform-origin origin ;IE 9 */
     :-webkit-transform rotate ;Chrome, Safari, Opera
     :-webkit-transform-origin origin}))

(def square-width 10)
(def square-space 200)
(def horizontal-squares (js/Math.round (/ (screen-width) square-space)))
(def vertical-squares (js/Math.round (/ (screen-height) square-space)))


(defn square-collection
  "create data that's used for generating background squares relative to player position"
  [x y]
    (flatten (for [squarex (range 0 (* square-width horizontal-squares) square-width)]
      (for [squarey (range 0 (* square-width vertical-squares) square-width)]
        {:x (* 20 squarex) :y (* 20 squarey)}))))

(defn square-element [square playerx playery]
  (dom/div #js {:style (clj->js (merge {:position "absolute"
                                        :background-color "red"
                                        :width square-width
                                        :height square-width}
                                       (square-position (:x square) (:y square) playerx playery)
                                       (square-rotation (:x square) (:y square) playerx playery)))} nil))

(defn background-view [player owner]
  (reify
    om/IRender
    (render [_]
            (apply dom/div #js {:className "fullscreen"
                                :style #js {:overflow "hidden"}}
                   (map #(square-element % (:x player) (:y player)) (square-collection 1 2))))))


;; game view/wrapper
;; ----------------------------------------------------------------------------

(defn game-view [state owner]
  (reify
    om/IRender
    (render [_]
            (dom/div nil
                     (om/build background-view (:player state))
                     (om/build player-coordinates (:player state))
                     (om/build player-view (:player state))))))

