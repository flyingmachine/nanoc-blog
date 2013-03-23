---
title: "How Clojure Babies are Made: Leiningen's Trampoline"
created_at: Mar 23 9:23:00 -0500 2013
kind: article
categories: programming, clojure
summary: An explanation of what lein trampoline does and how it does it.
additional_stylesheets:
  - pygments
---

In
[the last "How Clojure Babies are Made" article](/programming/how-clojure-babies-are-made-lein-run/)
I hinted that Leiningen avoids waste by using a trampoline. In this
installment of this awkwardly-named Clojure series, you'll learn what
the trampoline does and how it does it.

## The Rationale Behind the Trampoline

I know what you're thinking, wise-hearted reader. "Why would anyone
need to provide a rationale for a trampoline? That's like trying to
give a reason for rainbows, or a newborn's laughter, or Michael
Jackson's _Thriller_."

Allow me to explain myself. `lein trampoline` does indeed give you a
feeling of weightless freedom, just not in the way that you're used
to.

See, whenever you use Leiningen to run code from your project, you end
up with two Java processes running. Each process loads a separate
instance of the
[JVM](http://en.wikipedia.org/wiki/Java_virtual_machine). We saw this
in
[the previous article](/programming/how-clojure-babies-are-made-lein-run/)
in the output of `ps | grep lein`. The first process is for Leiningen
itself, and it's responsible for setting up everything necessary for
your project code to run. The second process is where your code
actually executes. If you were to run `lein -h`, you would only start
one Java process, as none of your project code would need to be
executed.

Leiningen starts a separate process for your project in order to
enforce isolation. This is because Leiningen is a true gentleman who
does not allow his namespaces and dependencies (like, say, a
completely different version of Clojure) to interfere with your
meticulously hand-crafted, artisinal program.

However, like a doting father, the Leiningen process continues to stay
open for the entire duration of your program's execution. If you have
a long-running process, like a web server for your creepy Justin
Bieber fan site, then Leiningen stays open the whole time consuming
memory that could be put to use in compositing images of the Biebs
with hearts all over them. 

This is where the trampoline comes into play. It allows the Leiningen
process to exit completely before your project's process starts. Now,
instead of two JVM's running, you only have one.

I think the name "trampoline" was chosen to evoke an image of
Leiningen providing a launching point for your program. However, I
choose to envision the trampoline process as follows:

1. Leiningen takes your program, which is embodied as one of those
   cymbal-clanging monkeys with unsettling eyes, and winds it up.
2. Leiningen gingerly places the wind-up monkey which is your program
   on the floor of a cozy Hobbit hole.
3. Leiningen steps outside and mounts a gigantic trampoline in Bilbo's
   front yard.
4. Leiningen takes two warm-up bounces and then, with a mighty
   "Hyup!", rockets himself into the sky, his mustaches flapping in
   the wind. He grows smaller and smaller until, with a bright
   sparkle, he disappears entirely.
5. Your creepy, noisey monkey of a web server starts doing whatever.

## How lein trampoline Works

Though you don't really need to understand how `lein trampoline` works
in order to use it, I think it's pretty cool. Below I walk you through
it step by step, with relevant code. We'll be using the project under
`leiningen/lein-build` of the
[make-a-clojure-baby github repo](https://github.com/flyingmachine/make-a-clojure-baby/tree/master/leiningen/lein-build).

1. Run `lein trampoline run` from the command line. If you're on a linux machine,
   this executes a
   [bash script](https://github.com/technomancy/leiningen/blob/6a70dc32362406be17189adc3c3a8d49e6594810/bin/lein).
   This script is probably at ~/bin/lein on your system.

2. The bash script
   [sets the `TRAMPOLINE_FILE`](https://github.com/technomancy/leiningen/blob/6a70dc32362406be17189adc3c3a8d49e6594810/bin/lein#L273)
   environment variable to a path. Later in this process, Leiningen
   will write a command to this file. Here's the part of the script
   that sets the environment variable:

   ``` shell
   if ([ "$LEIN_FAST_TRAMPOLINE" != "" ] || [ -r .lein-fast-trampoline ]) &&
       [ -r project.clj ]; then
       INPUTS="$@ $(cat project.clj) $LEIN_VERSION $(cat "$LEIN_HOME/profiles.clj")"
       INPUT_CHECKSUM=$(echo $INPUTS | shasum - | cut -f 1 -d " ")
       # Just don't change :target-path in project.clj, mkay?
       TRAMPOLINE_FILE="target/trampolines/$INPUT_CHECKSUM"
   else
       TRAMPOLINE_FILE="/tmp/lein-trampoline-$$"
       trap "rm -f $TRAMPOLINE_FILE" EXIT
   fi
   ```

3. The first Java process starts:

   ```
   /usr/bin/java \
     -client -XX:+TieredCompilation  \
     -Xbootclasspath/a:/Users/daniel/.lein/self-installs/leiningen-2.0.0-standalone.jar \
     -Dfile.encoding=UTF-8 \
     -Dmaven.wagon.http.ssl.easy=false \
     -Dleiningen.original.pwd=/Users/daniel/projects/web_sites/make-a-clojure-baby/leiningen/lein-build \
     -Dleiningen.script=/Users/daniel/bin/lein \
     -classpath :/Users/daniel/.lein/self-installs/leiningen-2.0.0-standalone.jar \
     clojure.main \ `# clojure.main is the entry point` \
     -m leiningen.core.main \
     trampoline run 1
   ```  

4. This causes Java to execute the `-main` method in `clojure.main`,
   which in turn loads `leiningen.core.main` and executes its `-main`
   function.

5. `leiningen.core.main/-main` applies the
   [trampoline task](https://github.com/technomancy/leiningen/blob/6a70dc32362406be17189adc3c3a8d49e6594810/src/leiningen/trampoline.clj#L44):

   ```clojure
   (defn ^:higher-order trampoline
     "Run a task without nesting the project's JVM inside Leiningen's.
   
   Calculates the Clojure code to run in the project's process for the
   given task and allows Leiningen's own JVM process to exit before
   running it rather than launching a subprocess of Leiningen's JVM.
   
   Use this to save memory or to work around stdin issues."
     [project task-name & args]
     (when (= :leiningen (:eval-in project))
       (main/info "Warning: trampoline has no effect with :eval-in-leiningen."))
     (binding [*trampoline?* true]
       (main/apply-task (main/lookup-alias task-name project)
                        (-> (assoc project :eval-in :trampoline)
                            (vary-meta update-in [:without-profiles] assoc
                                       :eval-in :trampoline))
                        args))
     (if (seq @eval/trampoline-forms)
       (write-trampoline project @eval/trampoline-forms @eval/trampoline-profiles)
       (main/abort task-name "did not run any project code for trampolining.")))
   ```

6. `trampoline` calls `leiningen.core.main/apply-task` but with a
   twist: it passes that function an updated project configuration,
   setting `:eval-in` to `:trampoline`. You can see this is the
   snippet above.

7. Eventually, `leiningen.core.eval/eval-in-project` gets applied. The
   cool thing about this function is that it then calls
   `leiningen.core.eval/eval-in`, which is a multi-method. It has
   different definitions for `:subprocess`, `:trampoline`, `:nrepl`,
   and a few more. This is one of the first times I've seen
   `defmethod` "in the wild" and it really tickled my pfeffernuesse.
   Definitely
   [check it out on github](https://github.com/technomancy/leiningen/blob/6a70dc32362406be17189adc3c3a8d49e6594810/leiningen-core/src/leiningen/core/eval.clj#L203).

   Since we updated our project configuration in the last step so that
   `:eval-in` is `:trampoline`, the `:trampoline` method gets matched:

   ```clojure
   (defmethod eval-in :trampoline [project form]
     (swap! trampoline-forms conj form)
     (swap! trampoline-profiles conj (select-keys project
                                                  [:dependencies :source-paths
                                                   :resource-paths :test-paths])))
   ```

   This updates the `trampoline-forms` and `trampoline-profiles` atoms
   within the `leiningen.core.eval` namespace.

8. The `trampoline` function shown in step 5 above continues
   executing:

   ```clojure
     (if (seq @eval/trampoline-forms)
       (write-trampoline project @eval/trampoline-forms @eval/trampoline-profiles)
       (main/abort task-name "did not run any project code for trampolining.")))
   ```   
  
   `write-trampoline` writes out the entire Java command necessary to
   finally run our project's `main` function. It writes this command
   to the path in the `TRAMPOLINE_FILE` environment variable set by
   the bash script in step 2 above.

9. The Leiningen process exits and the bash process from step 2
   continues. It checks for the existence of `TRAMPOLINE_FILE`, and
   since it exists, it essentially evaluates the command in it,
   kicking off the Java process which will run your code:
   
   ```shell
   if [ -r "$TRAMPOLINE_FILE" ] && [ "$LEIN_TRAMPOLINE_WARMUP" = "" ]; then
       TRAMPOLINE="$(cat $TRAMPOLINE_FILE)"
       if [ "$INPUT_CHECKSUM" = "" ]; then
           rm $TRAMPOLINE_FILE
       fi
       exec sh -c "exec $TRAMPOLINE"
   else
       exit $EXIT_CODE
   fi
   ```        

It's a bit of circuitous route, but Leiningen is not one to shy away
from hard work!

## The End

I hope you found this article interesting! The following topics are
next on my list of things to write about:

* How Leiningen manages dependencies
* How to distribute a full application
* How to distribute a library

So long for now, and may the lambda be with you!
