(ns gantry.server.zmq
  (:require [zeromq.zmq :as zmq]
            [taoensso.timbre :refer (info spy)]
            [clojure.core.async :refer [go <!!]]
            [gantry.server.channels :as channels]))


(defonce context (zmq/context 1))
(defonce sockets (atom {}))


(defn get-conn
  "Gets push socket for ZMQ"
  [id]
  (if (get @sockets id) nil
    (let [new-sock (doto (zmq/socket context :push)
                         (zmq/bind "tcp://*:5555"))]
      (info "Binding new zmq sock:" id)
      (swap! sockets assoc id new-sock)))
  (get @sockets id))


(defn pull-requests
  "Pulls requests off a channel and pushes them to zmq socket"
  [chan]

  (go (while true
        (let [req (<!! chan)]
          (info "Request!" (:id req))

          ; hur hur, lets do some work
          (Thread/sleep 1000)

          (channels/new-response (:id req)
                                 {:status 200
                                  :content-type "text/plain"
                                  :body "Hello, world"})

          ))))

