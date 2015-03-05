(ns clj-skii.util
  (:require [clojure.string :as string]
            [clojure.java.io :as io]))

;; TODO use prismatic/schema
;; TODO clojure.check above
;; (= (delinearize width (linearize width [r col :as coord])) coord)
(defn linearize
  [^Integer width [^Integer r ^Integer col]]
  {:pre [(< col width)]}
  "linearize a 2d structure to 1d"
  (+ (* r width) col))

(defn delinearize [^Integer width ^Integer idx]
  "destructure a 1d index to 2d coordination"
  [(quot idx width) (mod idx width)])

(defn get-in-map [{{:keys [width]} :size grid :grid} loc]
  (get grid (linearize width loc)))

;; skii map loading
(defn- parse-line [s]
  (map #(Integer/parseInt %) (string/split s #" ")))

(defn parse-map [name]
  (let [map-lines (line-seq (io/reader (io/resource name)))
        [width height] (parse-line (first map-lines))
        grid (mapcat #(parse-line %) (rest map-lines))]
    {:size {:width width :height height}
     :grid (into [] grid)}))
