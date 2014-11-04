(ns rhizo.data-example
  (:require [quil.core :as q]
            [clojure.string :as str]
            [clojure.pprint :refer (pprint)]
            [rhizo.core :refer (parse* get-system-svg azondi-g)]))

(def simple-svg-parsed (parse* (get-system-svg azondi-g)))
