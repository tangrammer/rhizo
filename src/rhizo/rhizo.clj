(ns rhizo.rhizo
  (:require [quil.core :as q]
            [clojure.string :as str]
               [clojure.pprint :refer (pprint)]
            [rhizo.core :refer (get-nodes simple-svg-parsed example-arrow get-arrows parse-path)]))


(def nodes (get-nodes simple-svg-parsed))

(def arrows (get-arrows simple-svg-parsed))

(defn setup []
  (q/smooth)
  (q/frame-rate 1)
  (q/background 200))


(defn draw-point [p]
  (q/push-style)
  (q/fill 0)
  (apply q/ellipse (let [[x y] p]
                     [x y 10 10]))
  (q/pop-style)
  )

(defn debug-print-points []
   (dorun
   (map (fn [{w :path}]
          (draw-point (:moveto w))
          (dorun
           (map draw-point (:absolute w))))
        arrows))
)

(defn draw-arrows []
  (q/push-style)
  (q/stroke 255  102 0)
  (q/no-fill)
  (dorun
   (map
    (fn [{example-arrow :path}]
      (->> (loop [curves []  moveto (:moveto example-arrow) absolute (partition 3 (:absolute example-arrow))]
             (let [[[a b c] & more] absolute
                   updated-curves (conj curves (flatten [moveto a b c]))]
               (if more
                 (recur updated-curves c  more)
                 updated-curves)))
           (map  #(apply q/bezier %))
           dorun)
      (draw-point (-> example-arrow :absolute last)))
    arrows
    ))
  (q/pop-style)
  )

(defn draw []
  (q/fill 250)
  (q/rect 0 0 (q/width)  (q/height))
  (q/translate 0 800)
  (q/stroke 0)
  (q/stroke-weight  1)
  (q/fill 200)

  (dorun
   (map
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

 ;;  (debug-print-points)
  (draw-arrows)

  )


;; TODO: scrollbar
;; https://processing.org/examples/scrollbar.htlm
(q/defsketch examplex
  :title "*rhizome component*"
  :setup setup
  :draw draw
  :size [1800 1200])
