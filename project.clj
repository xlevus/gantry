(defproject gantry "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/core.async "0.2.374"]
                 [com.taoensso/timbre "4.2.0"]
                 [org.zeromq/cljzmq "0.1.4"]
                 [http-kit "2.1.8"]]

  :main ^:skip-aot gantry.server.core
  :aliases {"server" ["run" "-m" "gantry.server.core"]
            "client" ["run" "-m" "gantry.client.core"]}

  :target-path "target/%s"

  :profiles {:uberjar {:aot :all}})
