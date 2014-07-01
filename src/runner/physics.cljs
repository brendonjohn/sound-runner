(ns runner.physics
  (:require [cljs.core.async :refer [chan <! >! timeout]])
  (:require-macros [cljs.core.async.macros :refer [go alt! go-loop]]))


;Setup our world
(def world (js/p2.World. (js-obj "gravity" #js [0 -9.82])))

(defn create-player [radius]
  (let [playerShape (js/p2.Circle. radius)
        playerBody (js/p2.Body. (js-obj "mass" 5 "position" #js [0 10]))]
    (.addShape playerBody playerShape)
    (.addBody world playerBody)
    ;return the body of the player
    playerBody))

;Create a plane
(defn create-plane [_]
  (let [groundShape (js/p2.Plane.)
        groundBody (js/p2.Body. (js-obj "mass" 0))]
    (.addShape groundBody groundShape)
    (.addBody world groundBody)
    groundBody))

(create-plane "lol")


(def physics-chan (chan))
(go (while true
      (<! (timeout 10))
      (.step world (/ 1 60))
      (>! physics-chan 1)))
