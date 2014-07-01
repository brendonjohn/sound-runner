(ns runner.physics
  (:require [cljs.core.async :refer [chan <! >! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go alt! go-loop]]))


;Setup our world
(def world (js/p2.World. (js-obj "gravity" #js [0 -9.82])))

;Create a plane
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

(def ground (create-plane "lol"))
(def main-player (create-player 1))

(contact-material ground main-player 10)


(def physics-chan (chan))
(go (while true
      (<! (timeout 10))
      (.step world (/ 1 60))
      (>! physics-chan 1)))


;; // When the materials of the plane and the first circle meet, they should yield
;;         // a contact friction of 0.3. We tell p2 this by creating a ContactMaterial.
;;         var cm = new p2.ContactMaterial(planeShape.material, shape.material, {
;;             friction : 0.3,
;;         });
;;         world.addContactMaterial(cm);


