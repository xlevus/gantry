(ns gantry.server.zmq
  (:require [clojure.core.async :refer [go <!!]]
            [clojure.edn :as edn]
            [zeromq.zmq :as zmq]
            [taoensso.timbre :refer (info spy)]
            [gantry.server.channels :as channels]))


(let [context (zmq/context 1)]

  (defn pull-requests
    "Pulls requests off a channel and pushes them to zmq socket"
    [chan]
    (info "pull-requests")

    (go (let [request-socket (doto (zmq/socket context :push)
                                   (zmq/bind "tcp://*:5555"))]
          (while true
            (let [request (<!! chan)]
              (info "Request!" (:id request))

              (zmq/send-str request-socket (pr-str request))

              ;(channels/new-response (:id request)
              ;                       {:status 200
              ;                        :content-type "text/plain"
              ;                        :body "Hello, world"})
              )))))


  (defn pull-responses []
    (info "pull-responses")
    (go (let [response-socket (doto (zmq/socket context :pull)
                                    (zmq/bind "tcp://*:5554"))]

          (while (not (.. Thread currentThread isInterrupted))
            (let [response (edn/read (zmq/receive-str response-socket))]
              (spy :debug response)
              (channels/new-response (:id response) (:response response)))))))

  (defn start []
     (pull-requests channels/incoming-requests)
     (pull-responses)
     ))
