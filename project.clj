(def metrics-version "2.9.0")
(defproject kixi/kixi.metrics "0.3.1-SNAPSHOT"
  :description "Provides mappers for jvm metrics to json and a Reporter for standard out"
  :url "http://github.com/MastodonC/kixi.metrics"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [cheshire "5.7.0"]
                 [metrics-clojure ~metrics-version]
                 [metrics-clojure-jvm ~metrics-version]]
  :repositories [["releases" {:url "https://clojars.org/repo"
                              :creds :gpg}]
                 ["snapshots" {:url "https://clojars.org/repo"
                               :creds :gpg}]])
