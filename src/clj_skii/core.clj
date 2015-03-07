(ns clj-skii.core
  (:require [clj-skii.graph :refer [build-graph find-longest-paths]]
            [clj-skii.util :refer [parse-map delinearize]])
  (:gen-class))

(defn compare-length [{l1 :length} {l2 :length}]
  (let [result (compare l1 l2)]
    (cond
      (> result 0) :great
      (= result 0) :equal
      (< result 0) :less)))

(defn steeper-route? [{d1 :drop} {d2 :drop}]
  (compare d1 d2))

(defn choose-best-routes [rs candidates]
  (let [r (first rs)
        candidate (first candidates)
        longest (condp = (compare-length r candidate)
                  :less candidates
                  :great rs
                  :equal (concat rs candidates))]
    (sort-by :drop > longest)))

(defn find-best-skii-routes [raw-map]
  (loop [search-seq (map first (sort-by second > (map-indexed vector (:grid raw-map))))
         visited #{}
         best-routes []]
    (if (empty? search-seq)
      best-routes
      (let [curr (delinearize (-> raw-map :size :width) (first search-seq))]
        (if (visited curr)
          (recur (rest search-seq)
                 visited
                 best-routes)
          (let [g (build-graph raw-map curr)
                route-candidates (find-longest-paths g curr)]
            (recur (rest search-seq)
                   (into visited (:vertexes g))
                   (choose-best-routes route-candidates best-routes))))))))


(defn -main [& args]
  (let [map-name (first args)
        skii-map (parse-map map-name)]
    (println "Best Route: " (first (find-best-skii-routes skii-map)))))
