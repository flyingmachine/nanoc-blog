(ns arenaverse.config)

(def ^:dynamic *aws-credentials* {:secret-key (System/getenv "AWS_SECRET")
                                  :access-key "SECRET!"})

(def env (get (System/getenv) "SERVER_ENV" "dev"))