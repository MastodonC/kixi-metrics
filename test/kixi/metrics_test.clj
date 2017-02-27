(ns kixi.metrics-test
  (:require [clojure.test :refer :all]
            [kixi.metrics :refer :all]
            [metrics
             [core :as m :refer [default-registry]]
             [counters :as c]
             [gauges :as g]
             [timers :as t]]
            [metrics.jvm.core :as jvm])
  (:import [com.codahale.metrics Counter]))

(deftest instrument-jvm-check-metrics
  (let [reg (m/new-registry)
        _ (jvm/instrument-jvm reg)
        metric-maps (registry->maps reg)]
    (is (= (count (filter :log-type metric-maps))
           (count metric-maps)))))

(deftest map-counter->map
  (let [reg (m/new-registry)
        counter (c/counter reg "a-counter")
        cs (map counter->map (m/counters reg))]
    (is (= {:type :counter
            :name "default.default.a-counter"
            :value 0}
           (first cs)))))

(deftest map-gauge->map
  (let [reg (m/new-registry)
        gauge (g/gauge-fn reg "a-gauge" (constantly 10))
        gs (map gauge->map (m/gauges reg))]
    (is (= {:type :gauge
            :name "default.default.a-gauge"
            :value 10}
           (first gs)))))

(deftest map-timer->map
  (let [reg (m/new-registry)
        timer (t/timer reg "a-timer")
        ts (mapcat timer->map (m/timers reg))]
    (is (= [{:type :timer
             :name "default.default.a-gauge"
             :value 10}]
           ts))))
