(ns clj-skii.core)

;; find the sequence of search
(sort-by second > (map-indexed vector (:grid skii-map)))


