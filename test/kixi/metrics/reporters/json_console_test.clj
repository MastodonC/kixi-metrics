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
        metric-maps (registry->maps reg)]
    (is (= (count (filter :log-type metric-maps))
           (count metric-maps)))))

(def zero-counter
  {:type :counter
   :name "default.default.a-counter"
   :value 0})

(deftest map-counter->map
  (let [reg (m/new-registry)
        counter (c/counter reg "a-counter")
        cs (map counter->map (m/counters reg))]
    (is (= zero-counter
           (first cs)))))

(def zero-gauge
  {:type :gauge
   :name "default.default.a-gauge"
   :value 10})

(deftest map-gauge->map
  (let [reg (m/new-registry)
        gauge (g/gauge-fn reg "a-gauge" (constantly 10))
        gs (map gauge->map (m/gauges reg))]
    (is (= zero-gauge
           (first gs)))))

(def zero-timer
  [{:type :timer, :name "default.default.a-timer.min", :value 0}
   {:type :timer, :name "default.default.a-timer.p50", :value 0.0}
   {:type :timer, :name "default.default.a-timer.p95", :value 0.0}
   {:type :timer, :name "default.default.a-timer.mean", :value 0.0}
   {:type :timer, :name "default.default.a-timer.p98", :value 0.0}
   {:type :timer, :name "default.default.a-timer.count", :value 0}
   {:type :timer, :name "default.default.a-timer.p99", :value 0.0}
   {:type :timer, :name "default.default.a-timer.std-dev", :value 0.0}
   {:type :timer, :name "default.default.a-timer.max", :value 0}
   {:type :timer, :name "default.default.a-timer.p75", :value 0.0}
   {:type :timer, :name "default.default.a-timer.p999", :value 0.0}
   {:type :timer, :name "default.default.a-timer.m1_rate", :value 0.0}
   {:type :timer, :name "default.default.a-timer.m5_rate", :value 0.0}
   {:type :timer, :name "default.default.a-timer.m15_rate", :value 0.0}
   {:type :timer, :name "default.default.a-timer.mean_rate", :value 0.0}])

(deftest map-timer->map
  (let [reg (m/new-registry)
        timer (t/timer reg "a-timer")
        ts (mapcat timer->map (m/timers reg))]
    (is (= (set zero-timer)
           (set ts)))))

(def zero-histo
  [{:type :histogram,
    :name "default.default.a-histogram.p999",
    :value 0.0} 
   {:type :histogram,
    :name "default.default.a-histogram.p50",
    :value 0.0}
   {:type :histogram,
    :name "default.default.a-histogram.p95",
    :value 0.0}
   {:type :histogram,
    :name "default.default.a-histogram.count",
    :value 0}
   {:type :histogram,
    :name "default.default.a-histogram.p75",
    :value 0.0}
   {:type :histogram,
    :name "default.default.a-histogram.min",
    :value 0}
   {:type :histogram,
    :name "default.default.a-histogram.mean",
    :value 0.0}
   {:type :histogram,
    :name "default.default.a-histogram.p99",
    :value 0.0}
   {:type :histogram,
    :name "default.default.a-histogram.max",
    :value 0}
   {:type :histogram,
    :name "default.default.a-histogram.p98",
    :value 0.0}])

(deftest map-histo->map
  (let [reg (m/new-registry)
        histo (h/histogram reg "a-histogram")
        hs (mapcat histo->map (m/histograms reg))]
    (is (= (set zero-histo)
           (set hs)))))

(def zero-meter
  [{:type :meter, :name "default.default.a-meter.m1_rate", :value 0.0}
   {:type :meter,
    :name "default.default.a-meter.mean_rate",
    :value 0.0}
   {:type :meter, :name "default.default.a-meter.m5_rate", :value 0.0}
   {:type :meter,
    :name "default.default.a-meter.m15_rate",
    :value 0.0}])

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
    (is (= (set (map #(assoc % :log-type :metric)
                     (flatten [zero-meter
                               zero-histo
                               zero-timer
                               zero-counter
                               zero-gauge])))
           (set all-metrcs)))))

