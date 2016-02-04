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
      (let [request-str (zmq/receive-str receiver)
            request (edn/read-string request-str)]

        (spy :debug request-str)
        (zmq/send-str sender (pr-str {:id (:id request)
                                      :response {:status 200
                                                 :content-type "text/plain"
                                                 :body "Hello, world"}}))
        ))))


(defn -main [& args]
  (let [context (zmq/zcontext 1)]
    (listen context)))

