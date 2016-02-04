(ns gantry.server.channels
  (:require [clojure.core.async :as async]
            [taoensso.timbre :refer (debug spy)]
            [org.httpkit.server :as httpkit]))


(defonce incoming-requests (async/chan 10))


(let [pending-responses (agent {})
      current-id (atom 0)]

  (defn new-request
    "Records a new request and returns the reqest-id"
    [request-channel]
    (let [req-id (swap! current-id inc)]
      (send pending-responses assoc req-id request-channel)
      req-id))

  (defn new-response
    "Records a response for the given request-id and sends it."
    [req-id response]

    (let [request-channel (get @pending-responses req-id)]
      (send pending-responses dissoc req-id)
      (httpkit/send! request-channel response)
      (httpkit/close request-channel))))

