(ns rhizo.core
  (:require
   [com.stuartsierra.dependency :as dep]
   [com.stuartsierra.component :as component]
   [clojure.pprint :refer (pprint)]
   [clojure.string :as str]
   [clojure.zip :as zip]
   [clojure.xml :as xml]
   [rhizome.viz :refer :all]
   [rhizome.dot :refer :all]

   ))

(defn zip-str [s]
  (zip/xml-zip
      (xml/parse (java.io.ByteArrayInputStream. (.getBytes s)))
  )
)
(defn view-system-image [g]

  (graph->svg (keys g) g
              :node->descriptor
              (fn [n] (merge {:label n
                             #_:style #_[:filled :rounded

                                     ]

                             :shape :rect}
                            ))
              :edge->descriptor
              (fn [src dst] {:label src

                            :fontsize 10


                            })
              :filename "azondi-messages.png"))

(def azondi-g
  {:reactor [], :oauth-access-token-store [:database], :api-request-authenticator [:api-key-request-authenticator :oauth-access-token-request-authenticator], :verification-code-store [:database], :webapp-listener [:webapp-router], :sse-debug [:mqtt-handler], :cljs-topic-browser [], :api [:database :cassandra :api-request-authenticator :password-verifier :emailer], :main-cljs-builder [:cljs-logo :cljs-main :cljs-core :cljs-topic-browser], :mqtt-encoder [], :password-verifier [:database :password-hash-algo], :emailer [], :webapp-session-store [:webapp-token-store], :authorization-server-http-listener [:authorization-server-webrouter], :web-resources [], :logout [:authorization-server-session-store], :oauth-client-registry [], :resource-ring-middleware [:resource-router], :authorization-server-webrouter [:web-resources :login :signup-form :authorization-server :reset-password :main-cljs-builder :logout], :cljs-main [], :authorization-server-session-store [:authorization-server-token-store], :authorization-server [:authorization-server-session-store :login :oauth-client-registry :oauth-access-token-store], :ws-bridge [:database :reactor], :resource-listener [:resource-ring-middleware], :webapp [:webapp-oauth-client :main-cljs-builder], :login [:authorization-server-session-store :database :user-form-renderer :password-verifier], :cassandra [], :mqtt-server [:mqtt-decoder :mqtt-encoder :mqtt-handler], :cljs-core [], :webapp-router [:webapp-oauth-client :webapp :main-cljs-builder], :state-store [], :webapp-token-store [:database], :user-form-renderer [], :mqtt-handler [:database :metrics :reactor], :database [], :reset-password [:authorization-server-session-store :database :user-form-renderer :verification-code-store :password-verifier :emailer], :sse-bridge [:database :api-request-authenticator :reactor], :signup-form [:authorization-server-session-store :database :user-form-renderer :verification-code-store :password-verifier :emailer], :cljs-logo [], :message-archiver [:cassandra :reactor], :oauth-access-token-request-authenticator [:oauth-access-token-store], :resource-router [:sse-debug :sse-bridge :api], :api-key-request-authenticator [:database], :mqtt-decoder [], :topic-injector [:database :reactor], :metrics [], :authorization-server-token-store [:database], :password-hash-algo [], :webapp-oauth-client [:webapp-session-store :state-store :oauth-client-registry]})

(def g
    {:a [:b :c]
         :b [:c]
     :c [:a]})

(def svg (view-system-image azondi-g))

(def simple-svg-parsed (zip-str svg))

(defn find-all-g-tag [parsed-svg k]
  (->> parsed-svg
       first :content
       first :content
       (filter (fn [{:keys [tag attrs]}]
                 (and (= (name k) (:class attrs)) (= tag :g))))))

(first  (find-all-g-tag simple-svg-parsed :edge))

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

(get-nodes simple-svg-parsed)

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

;; these lines show that we only have one M and one C in azondi arrow definition
(->> (get-arrows simple-svg-parsed)
     (map (fn [[a b _] ]  b))
     (map into-set-path-def)
;;     distinct
     )
(second (first (get-arrows simple-svg-parsed)))

;; moveto http://www.w3.org/TR/SVG/paths.html#PathDataMovetoCommands
;; follows absolute
(defn parse-path [s]
  (let [[moveto absolute] (str/split s #"C")]
    {:moveto (extract-point (str/replace moveto #"M" ""))
     :absolute (vec (extract-points absolute))}))

(parse-path "M1465.01,-83.896C1457.99,-74.7204 1452.79,-63.3996 1459,-54 1465.4,-44.31 1475.09,-37.2936 1485.59,-32.2159")





(defn get-arrows [parsed-svg]
  (map (fn [[title {{d :d} :attrs} {{points :points} :attrs} {[label] :content}]]
         {:label (read-string label)
          :path (parse-path d)
          :arrow (vec (extract-points points))})
       (map :content (find-all-g-tag parsed-svg :edge))))


(-> (get-arrows simple-svg-parsed)
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
