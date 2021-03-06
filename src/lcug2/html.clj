(ns lcug2.html
  (:require [clojure.java.io :refer [as-url]]
            [clojure.string :refer [lower-case ends-with?]]
            [net.cgrand.enlive-html :as e])
  (:import (java.net URL MalformedURLException)
           (java.io StringReader)))

(defn base [url]
  (let [url (as-url url)]
    (str (.getProtocol url) "://" (.getAuthority url))))

(defn resource [url]
  (let [url (as-url url)
        q (.getQuery url)]
    (str (.getPath (as-url url)) (if (seq q) "?" "") q)))

(defn in-context [ctx url]
  (let [ctx (URL. ctx)]
    (try
      (URL. ctx url)
      (catch MalformedURLException _))))

(defn links-from [html]
  (sequence
    (comp
      (map (comp :href :attrs))
      (remove nil?))
    (e/select html [:a])))

(defn words-from [html]
  (sequence
    (comp
      (mapcat (partial re-seq #"\w+"))
      (remove (partial re-matches #"\d+"))
      (map lower-case))
    (-> html
        (e/at [:script] nil)
        (e/select [:body e/text-node]))))

(defn parse [content]
  (e/html-resource (StringReader. content)))
