(defproject bond "0.1.0-SNAPSHOT"
            :description "The spy agent"
            :dependencies [[org.clojure/clojure "1.3.0"]
                           [clj-redis "0.0.12"]
                           [cheshire "4.0.0"]
                           [clj-time "0.4.2"]
                           [noir "1.2.1"]]
            :main bond.server)

