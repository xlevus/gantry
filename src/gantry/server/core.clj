(ns gantry.server.core
  (:require [org.httpkit.server :as httpkit]
            [clojure.core.async :refer [chan >!!]]
            [taoensso.timbre :refer (debug info spy)]
            [gantry.server.channels :as channels]
            [gantry.server.zmq :as zmq])
  (:gen-class))


(defonce server (atom nil))


(defn app [ring-request]
    (info (:request-method ring-request)
          (:uri ring-request))

    (httpkit/with-channel ring-request channel
      (let [req-id (channels/new-request channel)]
        (debug "ID:" req-id)
        (>!! channels/incoming-requests {:id req-id
                                         :request (dissoc ring-request :async-channel)}))))


(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))


(defn start-server
  "Starts the server"
  [port]
  (reset! server (httpkit/run-server app {:port port}))

  (zmq/start)

  (info "Gantry running on port" port))


(defn -main [& args]
  (start-server 8080))
