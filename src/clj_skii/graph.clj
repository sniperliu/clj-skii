(ns clj-skii.graph
  (:require [clj-skii.util :refer :all]))

;; skii problem become DAG problem

;; function to find the adjacents in graph
;; TODO try function generator later
(defn in-range [grid-map [r col]]
  (let [r-min 0
        r-max (-> grid-map :size :height)
        col-min 0
        col-max (-> grid-map :size :width)]
    (and (>= r r-min) (< r r-max) (>= col col-min) (< col col-max))))

(defn slope-down [grid-map pos1 pos2]
  "return true when pos1 is higher than pos2"
  (> (get-in-map grid-map pos1) (get-in-map grid-map pos2)))

;; TODO  try clojure.match later
(defn- step [region [r col :as old-position] move]
  (let [new-position (case move
                       :south [(inc r) col]
                       :north [(dec r) col]
                       :east  [r (inc col)]
                       :west  [r (dec col)]
                       old-position)]
    (if (and (in-range region new-position) (slope-down region old-position new-position))
      new-position
      nil)))

;; brute force with dfs
;; (defprotocol Graph
;;    (adjacent [graph v]))
(defn adjacent
  ([v] (adjacent skii-map v))
  ([graph v]
     "return adjacent vertexes of the parameter vertex"
     (filter (comp not nil?) (map #(step graph v %) [:south :east :north :west]))))

;; DFS to get vertex in topological order
(defn topological-sort [graph start-pos]
  "dfs and return topological order of the graph from a start point and vertex set (without start-point)"
  (loop [to-be-visit (adjacent graph start-pos)
         endpoints (into #{} to-be-visit)
         edge-to (reduce #(conj %1 [start-pos %2]) [] to-be-visit)
         adjacent-map (reduce #(assoc %1 %2 [start-pos]) {} to-be-visit)]
    (if (empty? to-be-visit)
      {:top-order edge-to :vertexes endpoints :g adjacent-map}
      (let [curr (first to-be-visit)
            adjacents (adjacent graph curr)]
        (recur (concat (rest to-be-visit) adjacents)
               (into endpoints adjacents)
               (concat edge-to
                       (reduce #(conj %1 [curr %2]) [] (filter (comp not endpoints) adjacents)))
               (merge-with (fn [x y] (concat x y))
                                adjacent-map
                                (reduce #(assoc %1 %2 [curr]) {} adjacents)))))))

(defn relax [])

(defn find-longest-path [graph start-pos]
  (let [{:keys [top-order verts adj-map]} (topological-sort graph start-pos)]
    (loop [order top-order
           distant (zipmap verts (repeat 0))]
      (map #() (adj-map (first order))))))
