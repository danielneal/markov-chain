(ns markov-chain.core
  (:require [clojure.string :as str]))

(defn map-vals
  "Map vals (taken from medley library)"
  [f coll]
  (persistent! (reduce-kv #(assoc! %1 %2 (f %3))
                          (transient (empty coll))
                          coll)))
(def alice
  (slurp "resources/alice.txt"))

(def bohemian
  (slurp "resources/bohemian.txt"))

(defn transform-source
  "Transforms a source text into a map of 2-word prefixes
  to a map of suffixes with their probabilites"
  [source]
  (map-vals (fn [vals]
              (let [suff (map second vals)
                    freq (doall (frequencies suff))
                    total (apply + (for [[a c] freq] c))]
                (map-vals #(/ % total) freq)))
            (group-by first
                      (map #(let [prefix (str (first %) " " (second %))
                                  suffix (nth % 2)]
                              [prefix suffix])
                           (partition 3 1
                                      (->> (str/split source #"[\s]")
                                           (map str/lower-case)))))))

(defn choose-word
  "Pick a random suffix from a map of suffixes to probabilities, according
  to the their probability distribution"
  [suffixes]
  "Draw following the given probability distribution"
  (let [r (rand)]
    (loop [[current & remaining] (into [] suffixes)
           upper-bound (current 1)]
      (let [[subsequent & _] remaining
            suffix (current 0)]
      (if (< r upper-bound)
        suffix
        (recur remaining (+ upper-bound (subsequent 1))))))))


(defn markov [source seed max-n]
  (let [source (transform-source source)]
    (loop [g (vec (str/split seed #"[\s]"))
           n 0]
      (let [last-2 (str (last (pop g)) " " (last g))
            next-word (try (choose-word (get source last-2))
                        (catch Exception e nil))]
        (if (or (> n max-n) (nil? next-word))
          (apply str (interpose " " g))
          (recur (conj g next-word) (inc n)))))))

;; usage
(markov alice "it was" 40)








