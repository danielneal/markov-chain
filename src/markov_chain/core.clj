(ns markov-chain.core
  (:require [clojure.string :as str]))

(defn map-vals
  [f coll]
  (persistent! (reduce-kv #(assoc! %1 %2 (f %3))
                          (transient (empty coll))
                          coll)))
(def alice
  (slurp "resources/alice.txt"))

(def bohemian
  (slurp "resources/bohemian.txt"))

(defn transform-source [source]
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

(transform-source bohemian)
(defn choose-word [probabilities]
  "Draw following the given probability distribution"
  (let [r (rand)]
    (loop [[current & remaining] (into [] probabilities)
           upper-bound (current 1)]
      (let [[subsequent & _] remaining
            suffix (current 0)]
      (if (< r upper-bound)
        suffix
        (recur remaining (+ upper-bound (subsequent 1))))))))

(def source alice)
(def source bohemian)

(let [input-prefix "i'm just"
      source (transform-source source)
      max-n 30]
  (loop [g (vec (str/split input-prefix #"[\s]"))
         n 0]
    (let [last-2 (str (last (pop g)) " " (last g))
          next-word (try (choose-word (get source last-2))
                      (catch Exception e nil))]
      (if (or (> n max-n) (nil? next-word))
        (apply str (interpose " " g))
        (recur (conj g next-word) (inc n))))))








