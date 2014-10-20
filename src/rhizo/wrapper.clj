(ns rhizo.wrapper)

(defprotocol Welcome
  (greetings [_] "add your message in your impl"))

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!"))

(println (greetings (Example.)))

(defn wrap-this [r]
  (reify
    Welcome
    (greetings [_]
      (str "with wrapper: " (greetings r)))))

(println (greetings (wrap-this (Example.))))
