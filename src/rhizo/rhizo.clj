(ns rhizo.rhizo
  (:require [quil.core :as q]
            [clojure.string :as str]
               [clojure.pprint :refer (pprint)]
            [rhizo.core :refer (get-nodes simple-svg-parsed)]))


(def nodes (get-nodes simple-svg-parsed))

(defn setup []
  (q/smooth)
  (q/frame-rate 1)
  (q/background 200)


  )

(defn draw []
  (q/fill 200)
  (q/rect 0 0 (q/width)  (q/height))
  (q/translate 0 (q/height) )
  (q/stroke 255)
  (q/stroke-weight  1)
  (q/fill 100)

  (dorun (map
          (fn [{label :label points :points [name-x name-y] :label-pos}]
            (q/begin-shape)
            (doseq [[x y] points]
              (q/vertex x y))
            (q/end-shape :close)
            (q/push-style)
            (q/fill 0)
            (q/text (str label) name-x name-y)
            (q/pop-style))
          nodes))

  ;(q/rect-mode :center)
  ;(q/rect 100 100 (q/random 100) (q/random 100))
)

(q/defsketch examplex
  :title "*rhizome component*"
  :setup setup
  :draw draw
  :size [1800 1200])
