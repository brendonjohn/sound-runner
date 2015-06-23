(defproject runner "0.1.0"
  :description "js game that rides the radio... maybe one day"
  :url "https://github.com/brendonjohn/sound-runner.git"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2234"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [om "0.6.4"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]
  :resource-paths ["bower_components"]

  :cljsbuild {:builds [{:id "runner"
                        :source-paths ["src"]
                        :compiler {:output-to "runner.js"
                                   :output-dir "out"
                                   :optimizations :none
                                   :source-map true}}
                       {:id "dist"
                        :source-paths ["src"]
                        :compiler {:output-to "dist/runner.js"
                                   :optimizations :simple
                                   :pretty-print false
                                   :preamble ["react/react.min.js"
                                              "p2js/build/p2.min.js"]}}]})
