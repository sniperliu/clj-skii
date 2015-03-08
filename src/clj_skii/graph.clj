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
(defn- step [region [r col :as old-position] direction slope-fn]
  (let [new-position (case direction
                       :south [(inc r) col]
                       :north [(dec r) col]
                       :east  [r (inc col)]
                       :west  [r (dec col)]
                       old-position)]
    (if (and (in-range region new-position) (slope-fn region old-position new-position))
      new-position
      nil)))

;; (defprotocol Graph
;;    (adjacent [graph v]))
(defn outbound [graph v]
  "return down vertexes of the parameter vertex"
  (filter (comp not nil?) (map #(step graph v % slope-down) [:south :east :north :west])))

;; DFS to build graph first
(defn build-graph [raw-map start-pos]
  "build graph from map with a start point specified"
  (loop [visited #{start-pos}
         to-be-visit (outbound raw-map start-pos)
         edge-set (reduce #(conj %1 [start-pos %2]) #{} (outbound raw-map start-pos))]
    (if (empty? to-be-visit)
      {:vertexes visited :edges edge-set :raw raw-map}
      (let [curr (first to-be-visit)]
        (if (visited curr)
          (recur visited
                 (rest to-be-visit)
                 edge-set)
          (recur (conj visited curr)
                 (concat (outbound raw-map curr) (rest to-be-visit))
                 (into edge-set (reduce #(conj %1 [curr %2]) #{} (outbound raw-map curr)))))))))

;; run topological sort to get vertex in order
(defn inbound [edges v]
  (filter (fn [[_ p2]] (= v p2)) edges))

(defn topological-sort [{:keys [edges raw] :as graph} start-pos]
  (loop [ordered '()
         pre-set (conj '() start-pos)
         edges-set edges]
    (if (empty? pre-set)
      (reverse ordered)
      (let [pre-v (first pre-set)
            adjacents (outbound raw pre-v) ;; use the trick here instead use edges-set
            outbound-edge (into #{} (map vector (repeat pre-v) adjacents))
            new-edges-set (remove outbound-edge edges-set)
            new-pre-set (filter (comp empty? (partial inbound new-edges-set)) adjacents)]
        (recur (conj ordered pre-v)
               (concat new-pre-set (rest pre-set))
               new-edges-set)))))

(defn relax [distant [from to]]
  (if (< (distant to) (+ (distant from) 1))
    (assoc distant to (+ (distant from) 1))
    distant))

(defn find-longest-paths [graph start-pos]
  (let [{:keys [vertexes raw]} graph
        top-order (topological-sort graph start-pos)
        top-edges (mapcat #(map vector (repeat %) (outbound raw %)) top-order)
        distant (merge (zipmap vertexes (repeat -1)) {start-pos 1})
        rs (sort-by second > (reduce relax distant top-edges))
        l (second (first rs))
        candidates (take-while #(= l (second %)) rs)]
    (map (fn [[end-pos dist]]
           {:start start-pos :end end-pos :length dist
            :drop (- (get-in-map raw start-pos) (get-in-map raw end-pos))})
         candidates)))
