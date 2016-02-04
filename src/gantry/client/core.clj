(ns gantry.client.core
  (:require [clojure.edn :as edn]
            [zeromq.zmq :as zmq]))


(defn listen [context]
  (with-open [receiver (doto
                         (zmq/socket context :pull)
                         (zmq/connect "tcp://127.0.0.1:5555"))]
    (println "Listening...")

    (while (not (.. Thread currentThread isInterrupted))
      (let [request (zmq/receive-str receiver)]
        (println request)))))


(defn -main [& args]
  (let [context (zmq/zcontext 1)]
    (listen context)))

