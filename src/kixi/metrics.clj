(ns kixi.metrics
  (:require [metrics
             [core :as m :refer [new-registry default-registry]]]
            [metrics.ring.expose :as expose]
            [cheshire.core :as json]
            [metrics.counters :as c]
            [metrics.timers :as t])
  (:import [com.codahale.metrics ScheduledReporter MetricFilter]
           [java.util.concurrent TimeUnit]))

(defn gauge->map
  [[gauge-name gauge]]
  {:type :gauge
   :name gauge-name
   :value (g/value gauge)})

(defn counter->map
  [[gauge-name gauge]]
  {:type :counter
   :name gauge-name
   :value (c/value gauge)})

(def timer-fields
  {"smallest" t/smallest
   "largest" t/largest
   "mean" t/mean
   "std-dev" t/std-dev})

(defn timer->map
  [[timer-name timer]]
  (map
   (fn [[n f]]
     {:type :timer
      :name (str timer-name "." n)
      :value (f timer)})
   timer-fields))

(defn registry->maps
  [registry]
  (let [gauges (m/gauges registry)
        counters (m/counters registry)
        histos (m/histograms registry)
        meters (m/meters registry)
        timers (m/timers registry)]
    (map 
     #(assoc % 
             :log-type :metric)
     (concat
                                        ;(map gauge->map gauges)
      (map counter->map counters)))))

(defn attach-custom-reporter
  ([]
   (attach-custom-reporter default-registry))
  ([registry]
   (let [name "Json-console-reporter"
         poll 5
         poll-unit TimeUnit/SECONDS
         filter MetricFilter/ALL
         rateUnit TimeUnit/SECONDS
         durationUnit TimeUnit/MILLISECONDS
         console-json-reporter (proxy [ScheduledReporter] [registry name filter rateUnit durationUnit]
                                 (report
                                   ([]
                                    (let [metric-maps (registry->maps registry)]
                                      (json/generate-stream
                                       metric-maps
                                       *out*)
                                      (prn)))))]
     (.start console-json-reporter
             poll poll-unit))))

(defn configure-metrics
  []
  (let [reg default-registry]
    (attach-custom-reporter reg)
;    (jvm/instrument-jvm reg)
    ))
