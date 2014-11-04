(ns rhizo.core
  (:require
   [clojure.pprint :refer (pprint)]
   [clojure.string :as str]
   [clojure.zip :as zip]
   [clojure.xml :as xml]
   [rhizome.viz :refer :all]
   [rhizome.dot :refer :all]))

(def current-azondi {:reactor [], :oauth-access-token-store [:database], :api-request-authenticator [:oauth-access-token-request-authenticator :api-key-request-authenticator], :verification-code-store [:database], :webapp-listener [:webapp-router], :sse-debug [:mqtt-handler], :cljs-topic-browser [], :api [:api-request-authenticator :database :cassandra :emailer :password-verifier], :main-cljs-builder [:cljs-main :cljs-logo :cljs-topic-browser :cljs-core], :mqtt-encoder [], :password-verifier [:database :password-hash-algo], :emailer [], :webapp-session-store [:webapp-token-store], :authorization-server-http-listener [:authorization-server-webrouter], :web-resources [], :logout [:authorization-server-session-store], :oauth-client-registry [], :resource-ring-middleware [:resource-router], :authorization-server-webrouter [:main-cljs-builder :web-resources :login :signup-form :reset-password :authorization-server :logout], :cljs-main [], :authorization-server-session-store [:authorization-server-token-store], :authorization-server [:login :authorization-server-session-store :oauth-access-token-store :oauth-client-registry], :ws-bridge [:database :reactor], :resource-listener [:resource-ring-middleware], :webapp [:main-cljs-builder :webapp-oauth-client], :login [:authorization-server-session-store :database :user-form-renderer :password-verifier], :cassandra [], :mqtt-server [:mqtt-handler :mqtt-decoder :mqtt-encoder], :cljs-core [], :webapp-router [:main-cljs-builder :webapp :webapp-oauth-client], :state-store [], :webapp-token-store [:database], :user-form-renderer [], :mqtt-handler [:metrics :database :reactor], :database [], :reset-password [:authorization-server-session-store :database :user-form-renderer :verification-code-store :emailer :password-verifier], :sse-bridge [:api-request-authenticator :database :reactor], :signup-form [:authorization-server-session-store :database :user-form-renderer :verification-code-store :emailer :password-verifier], :cljs-logo [], :message-archiver [:cassandra :reactor], :oauth-access-token-request-authenticator [:oauth-access-token-store], :resource-router [:api :sse-debug :sse-bridge], :api-key-request-authenticator [:database], :mqtt-decoder [], :topic-injector [:database :reactor], :metrics [], :authorization-server-token-store [:database], :password-hash-algo [], :webapp-oauth-client [:webapp-session-store :state-store :oauth-client-registry]})

(defn zip-str [s]
  (zip/xml-zip
      (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))
  )
)
(defn get-system-svg [g]
  (graph->svg (keys g) g
              :node->descriptor
              (fn [n] (merge {:label n
                             :shape :rect}))
              :edge->descriptor
              (fn [src dst] {:label src
                            :fontsize 10})))

(def azondi-g
  {:reactor [], :oauth-access-token-store [:database], :api-request-authenticator [:api-key-request-authenticator :oauth-access-token-request-authenticator], :verification-code-store [:database], :webapp-listener [:webapp-router], :sse-debug [:mqtt-handler], :cljs-topic-browser [], :api [:database :cassandra :api-request-authenticator :password-verifier :emailer], :main-cljs-builder [:cljs-logo :cljs-main :cljs-core :cljs-topic-browser], :mqtt-encoder [], :password-verifier [:database :password-hash-algo], :emailer [], :webapp-session-store [:webapp-token-store], :authorization-server-http-listener [:authorization-server-webrouter], :web-resources [], :logout [:authorization-server-session-store], :oauth-client-registry [], :resource-ring-middleware [:resource-router], :authorization-server-webrouter [:web-resources :login :signup-form :authorization-server :reset-password :main-cljs-builder :logout], :cljs-main [], :authorization-server-session-store [:authorization-server-token-store], :authorization-server [:authorization-server-session-store :login :oauth-client-registry :oauth-access-token-store], :ws-bridge [:database :reactor], :resource-listener [:resource-ring-middleware], :webapp [:webapp-oauth-client :main-cljs-builder], :login [:authorization-server-session-store :database :user-form-renderer :password-verifier], :cassandra [], :mqtt-server [:mqtt-decoder :mqtt-encoder :mqtt-handler], :cljs-core [], :webapp-router [:webapp-oauth-client :webapp :main-cljs-builder], :state-store [], :webapp-token-store [:database], :user-form-renderer [], :mqtt-handler [:database :metrics :reactor], :database [], :reset-password [:authorization-server-session-store :database :user-form-renderer :verification-code-store :password-verifier :emailer], :sse-bridge [:database :api-request-authenticator :reactor], :signup-form [:authorization-server-session-store :database :user-form-renderer :verification-code-store :password-verifier :emailer], :cljs-logo [], :message-archiver [:cassandra :reactor], :oauth-access-token-request-authenticator [:oauth-access-token-store], :resource-router [:sse-debug :sse-bridge :api], :api-key-request-authenticator [:database], :mqtt-decoder [], :topic-injector [:database :reactor], :metrics [], :authorization-server-token-store [:database], :password-hash-algo [], :webapp-oauth-client [:webapp-session-store :state-store :oauth-client-registry]})

(def g
    {:a [:b :c]
         :b [:c]
     :c [:a]})

#_(def svg (get-system-svg azondi-g))

(defn parse* [svg]
  (zip-str svg))



(defn find-all-g-tag [parsed-svg k]
  (->> parsed-svg
       first :content
       first :content
       (filter (fn [{:keys [tag attrs]}]
                 (and (= (name k) (:class attrs)) (= tag :g))))))
#_(def simple-svg-parsed (parse* svg) )
#_(first  (find-all-g-tag simple-svg-parsed :edge))

(defn extract-point [s]
  (map read-string (str/split s #",")))

(defn extract-points [s]
  (->>
   (str/split s #" ")
   (map extract-point)))

(defn get-nodes [parsed-svg]
  (let [svg-nodes (find-all-g-tag parsed-svg :node)]
    (map (fn [{[title {{points :points} :attrs} text] :content} ]
           {:label (read-string (get-in text [:content 0]))
            :label-pos (map read-string ((juxt :x :y) (:attrs text)))
            :points (extract-points points)}
           ) svg-nodes)
    ))

#_(get-nodes simple-svg-parsed)

#_(def nodes-example
    {:tag :g,
     :attrs {:id "node1", :class "node"},
     :content
     [{:tag :title, :attrs nil, :content ["node8584"]}
      {:tag :polygon,
       :attrs
       {:fill "none",
        :stroke "black",
        :points "54,-180 0,-180 0,-144 54,-144 54,-180"},
       :content nil}
      {:tag :text,
       :attrs
       {:text-anchor "middle",
        :x "27",
        :y "-158.3",
        :font-family "Monospace",
        :font-size "14.00"},
       :content [":c"]}]})

#_(def edges-example
    {:tag :g,
     :attrs {:id "edge1", :class "edge"},
     :content
     [{:tag :title, :attrs nil, :content ["node29842->node29844"]}
      {:tag :path,
       :attrs
       {:fill "none",
        :stroke "black",
        :d
        "M39.5729,-167.863C34.4802,-162.73 29.7624,-156.669 27,-150 24.459,-143.865 23.5334,-136.882 23.453,-130.209"},
       :content nil}
      {:tag :polygon,
       :attrs
       {:fill "black",
        :stroke "black",
        :points
        "26.9527,-130.294 23.9117,-120.145 19.9599,-129.976 26.9527,-130.294"},
       :content nil}
      {:tag :text,
       :attrs
       {:text-anchor "middle",
        :x "33.5",
        :y "-141.5",
        :font-family "Monospace",
        :font-size "10.00"},
       :content [":c"]}]})



;; -> testing regex http://rubular.com/
;; -> http://stackoverflow.com/questions/15814592/how-do-i-include-negative-decimal-numbers-in-this-regular-expression
(defn into-set-path-def [s] (re-seq   #"[^-\d).,\s]" s))
(re-seq   #"[^-\d).,\s]" "M1465.01,-83.896C1457.99,-74.7204 1452.79,-63.3996 1459,-54 1465.4,-44.31 1475.09,-37.2936 1485.59,-32.2159")


;; moveto http://www.w3.org/TR/SVG/paths.html#PathDataMovetoCommands
;; follows absolute
(defn parse-path [s]
  (let [[moveto absolute] (str/split s #"C")]
    {:moveto (extract-point (str/replace moveto #"M" ""))
     :absolute (vec (extract-points absolute))}))

(def example-arrow (parse-path "M1465.01,-83.896C1457.99,-74.7204 1452.79,-63.3996 1459,-54 1465.4,-44.31 1475.09,-37.2936 1485.59,-32.2159"))


(count (:absolute (parse-path "M335.105,-275.865C317.651,-254.884 294.306,-218.834 312,-192 385.653,-80.302 462.486,-113.453 593,-84 764.586,-45.2787 1308.14,-25.9638 1484.88,-20.593")))


(defn get-arrows [parsed-svg]
  (map (fn [[title {{d :d} :attrs} {{points :points} :attrs} {[label] :content}]]
         {:label (read-string label)
          :path (parse-path d)
          :arrow (vec (extract-points points))})
       (map :content (find-all-g-tag parsed-svg :edge))))

;; these lines show that we only have one M and one C in azondi arrow definition
#_(->> (get-arrows simple-svg-parsed)
     (map (fn [[a b _] ]  b))
     (map into-set-path-def)
     ;;     distinct
     )
#_(second (first (get-arrows simple-svg-parsed)))



#_(-> (get-arrows simple-svg-parsed)
    first

    )


(comment
  (= '(:tag :attrs :content)
     (-> simple-svg-parsed
         first
         :content
         first
         keys))

  (= 9
     (-> simple-svg-parsed
         first
         :content
         first
         :content
         count
         ))

  (= '(:title :polygon :g :g :g :g :g :g :g)
     (map :tag
          (-> simple-svg-parsed
              first
              :content
              first
              :content

              )))



  (= '(:title :path :polygon :text)
     (map :tag
          (-> simple-svg-parsed
              first
              :content
              first
              :content
              butlast


              ))))
