(ns bond.views.common
  (:use [noir.core :only [defpartial]]
        [hiccup.page-helpers :only [include-css include-js html5]]))

(defpartial layout [& content]
            (html5
              [:head
               [:title "Bond - the spy agent"]
               ;;(include-css "/css/reset.css")
               (include-css "/js/rickshaw.min.css")
               (include-css "/css/bond.css")
               ]
              [:body
               [:div#chart_container
                [:div#y_axis]
                [:div#chart content]
                [:div#legend]
                
                ]
               (include-js "/js/d3.min.js")
               (include-js "/js/d3.layout.min.js")
               (include-js "/js/rickshaw.min.js")
               (include-js "/js/bond.js")
               ]))

(defpartial json [data]
    [:script (str "var data = " data)])
