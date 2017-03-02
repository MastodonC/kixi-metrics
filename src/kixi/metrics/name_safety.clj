(ns kixi.metrics.name-safety
  (:require [clojure.string :as str]))

;; Lifted all this from silcon-gorge/radix
;; Generally useful for creating metrics for uri's, but good for any dynamic metric name creation.

(def replace-guid
  [#"[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}" "GUID"])

(def replace-number
  [#"[0-9][0-9]+" "NUMBER"])

(def replace-territory
  [#"/[a-z]{2}/" "/TERRITORIES/"])

(defn- apply-regex
  [path [pattern replacement]]
  (str/replace path pattern replacement))

(defn- apply-aggregations
  [path aggregations]
  (reduce apply-regex path aggregations))

;perf: replace with stringbuild and do all these in one pass
(defn clean-metric-name
  [name]
  (-> name
      (str/replace " " ".")
      (str/replace "./" ".")
      (str/replace "/" ".")
      (str/replace #"\p{Cntrl}" "")
      (str/replace #"^\.+" "")
      (str/replace #"\.+$" "")))

(def default-aggregations [replace-guid replace-number replace-territory])

(defn safe-name
  [unsafe-name]
  (when unsafe-name
    (-> unsafe-name
        clean-metric-name
        (apply-aggregations default-aggregations))))
