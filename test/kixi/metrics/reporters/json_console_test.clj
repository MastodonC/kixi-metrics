(ns kixi.metrics.reporters.json-console-test
  (:require [clojure.test :refer :all]
            [kixi.metrics.reporters.json-console :refer :all]
            [metrics
             [core :as m :refer [default-registry]]
             [counters :as c]
             [gauges :as g]
             [timers :as t]
             [histograms :as h]
             [meters :as meter]]
            [metrics.jvm.core :as jvm])
  (:import [com.codahale.metrics Counter]))

(deftest instrument-jvm-check-metrics
  (let [reg (m/new-registry)
        _ (jvm/instrument-jvm reg)
        metric-map (registry->maps reg)]
    (is (= (:logtype metric-map)
           :metric))
    (is (= (count (keys metric-map))
           63))))

(def zero-counter
  {"default.default.a-counter" 0})

(deftest map-counter->map
  (let [reg (m/new-registry)
        counter (c/counter reg "a-counter")
        cs (map counter->map (m/counters reg))]
    (is (= zero-counter
           (first cs)))))

(def zero-gauge
  {"default.default.a-gauge" 10})

(deftest map-gauge->map
  (let [reg (m/new-registry)
        gauge (g/gauge-fn reg "a-gauge" (constantly 10))
        gs (map gauge->map (m/gauges reg))]
    (is (= zero-gauge
           (first gs)))))

(def zero-timer
  {"default.default.a-timer.min" 0
   "default.default.a-timer.p50" 0.0
   "default.default.a-timer.p95" 0.0
   "default.default.a-timer.mean" 0.0
   "default.default.a-timer.p98" 0.0
   "default.default.a-timer.count" 0
   "default.default.a-timer.p99" 0.0
   "default.default.a-timer.std-dev" 0.0
   "default.default.a-timer.max" 0
   "default.default.a-timer.p75" 0.0
   "default.default.a-timer.p999" 0.0
   "default.default.a-timer.m1_rate" 0.0
   "default.default.a-timer.m5_rate" 0.0
   "default.default.a-timer.m15_rate" 0.0
   "default.default.a-timer.mean_rate" 0.0})

(deftest map-timer->map
  (let [reg (m/new-registry)
        timer (t/timer reg "a-timer")
        ts (mapcat timer->map (m/timers reg))]
    (is (= (set zero-timer)
           (set ts)))))

(def zero-histo
  {"default.default.a-histogram.p999" 0.0 
   "default.default.a-histogram.p50" 0.0
   "default.default.a-histogram.p95" 0.0
   "default.default.a-histogram.count" 0
   "default.default.a-histogram.p75" 0.0
   "default.default.a-histogram.min" 0
   "default.default.a-histogram.mean" 0.0
   "default.default.a-histogram.p99" 0.0
   "default.default.a-histogram.max" 0
   "default.default.a-histogram.p98" 0.0})

(deftest map-histo->map
  (let [reg (m/new-registry)
        histo (h/histogram reg "a-histogram")
        hs (mapcat histo->map (m/histograms reg))]
    (is (= (set zero-histo)
           (set hs)))))

(def zero-meter
  {"default.default.a-meter.m1_rate" 0.0
   "default.default.a-meter.mean_rate" 0.0
   "default.default.a-meter.m5_rate" 0.0
   "default.default.a-meter.m15_rate" 0.0})

(deftest map-meter->map
  (let [reg (m/new-registry)
        meter (meter/meter reg "a-meter")
        ms (mapcat meter->map (m/meters reg))]
    (is (= (set zero-meter)
           (set ms)))))

(deftest all-metrics
  (let [reg (m/new-registry)
        meter (meter/meter reg "a-meter")
        histo (h/histogram reg "a-histogram")
        timer (t/timer reg "a-timer")
        gauge (g/gauge-fn reg "a-gauge" (constantly 10))
        counter (c/counter reg "a-counter")
        all-metrcs (registry->maps reg)]
    (is (= (assoc (apply merge [zero-meter
                                zero-histo
                                zero-timer
                                zero-counter
                                zero-gauge])
                  :logtype :metric)
           all-metrcs))))

