(ns runner.physics
  (:require [cljs.core.async :refer [chan <! >! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go alt! go-loop]]))


;; hello physics world
;; ----------------------------------------------------------------------------
(def world (js/p2.World. (js-obj "gravity" #js [0 -9.82])))

;; physics functions - all of these have a side-affect :'(
;; ----------------------------------------------------------------------------

(defn create-plane [_]
  (let [groundShape (js/p2.Plane.)
        groundBody (js/p2.Body. (js-obj "mass" 0))]
    (set! (.-material groundShape) (js/p2.Material.))
    (.addShape groundBody groundShape)
    (.addBody world groundBody)
    groundBody))

(defn create-player [radius]
  (let [playerShape (js/p2.Circle. radius)
        playerBody (js/p2.Body. (js-obj "mass" 1 "position" #js [0 10]))]
    (set! (.-material playerShape) (js/p2.Material.))
    (.addShape playerBody playerShape)
    (.addBody world playerBody)
    ;return the body of the player
    playerBody))

(defn contact-material [shape1 shape2 friction]
  (.addContactMaterial world (js/p2.ContactMaterial. (.-material (first (.-shapes shape1)))
                                                     (.-material (first (.-shapes shape2)) )
                                                     (js-obj "friction" friction))))

;; add bodies to the world
;; ----------------------------------------------------------------------------
(def ground (create-plane "lol"))
(def main-player (create-player 1))

(contact-material ground main-player 100)

;; physics loop
;; ----------------------------------------------------------------------------

(def physics-chan (chan))
(go (while true
      (<! (timeout 10))
      (.step world (/ 1 60))
      (>! physics-chan 1)))

