(ns kixi.metrics.name-safety-test
  (:require [kixi.metrics.name-safety :as sut]
            [clojure.test :refer :all]))

(deftest ensure-uri-names-are-clean
  (let [uri "file/08eb375b-bf92-40cc-bced-e7bf74a7ceba/meta"]
    (is (= (sut/safe-name uri)
           "file.GUID.meta"))))
