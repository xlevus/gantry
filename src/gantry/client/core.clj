(ns gantry.client.core
  (:require [clojure.edn :as edn]
            [taoensso.timbre :refer (debug spy)]
            [zeromq.zmq :as zmq]))


(defn listen [context]
  (with-open [receiver (doto (zmq/socket context :pull)
                             (zmq/connect "tcp://127.0.0.1:5555"))
              sender (doto (zmq/socket context :push)
                           (zmq/connect "tcp://127.0.0.1:5554"))]
    (println "Listening...")

    (while (not (.. Thread currentThread isInterrupted))
      (let [request (zmq/receive-str receiver)]


        (spy :debug (zmq/send-str sender (pr-str {:id (:id request)
                                      :response {:status 200
                                                 :content-type "text/plain"
                                                 :body "Hello, world"}})))
        (debug "Request: " request)
        ))))


(defn -main [& args]
  (let [context (zmq/zcontext 1)]
    (listen context)))

